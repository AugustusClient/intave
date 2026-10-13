/*
 * Copyright 2026 Intave
 *
 * This software is licensed under the PolyForm Perimeter License 1.0.0.
 * You may use this software for any purpose, except for providing to
 * others any product that competes with the software.
 *
 * A copy of the license is available at:
 *   https://polyformproject.org/licenses/perimeter/1.0.0/
 */

package de.jpx3.intave.module.linker.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.jpx3.intave.IntaveLogger;
import de.jpx3.intave.packet.reader.PacketReader;
import de.jpx3.intave.packet.reader.PacketReaders;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Invokes a packet subscription method with an arbitrary combination of supported parameters
 * through a {@link MethodHandle}, resolving each argument with a resolver chosen at link time.
 */
final class MethodHandleSubscriptionExecutor implements PacketSubscriptionMethodExecutor {
  private static final ArgumentResolver NONE = (event, reader) -> null;

  private final Method calledMethod;
  private final MethodHandle handle;
  private final ArgumentResolver[] resolvers;
  private final int arity;
  private final boolean requiresReader;
  private final Function<? super PacketContainer, ? extends PacketReader> readerFactory;
  private final Consumer<String> skipLog;
  private volatile boolean block;

  private MethodHandleSubscriptionExecutor(
    Method calledMethod,
    Function<? super Player, ? extends User> userLookup,
    Function<? super PacketContainer, ? extends PacketReader> readerFactory,
    Consumer<String> skipLog
  ) {
    this.calledMethod = calledMethod;
    this.readerFactory = readerFactory;
    this.skipLog = skipLog;
    Class<?>[] parameterTypes = calledMethod.getParameterTypes();
    this.arity = parameterTypes.length;
    this.handle = handleOf(calledMethod);

    int playerParameterIndex = findParameterPosition(parameterTypes, Player.class);
    int userParameterPosition = findParameterPosition(parameterTypes, User.class);
    int cancelableParameterPosition = findParameterPosition(parameterTypes, Cancellable.class);
    int packetContainerParameterPosition = findParameterPosition(parameterTypes, PacketContainer.class);
    int packetReaderParameterPosition = findParameterPosition(parameterTypes, PacketReader.class);
    int packetEventParameterPosition = findParameterPosition(parameterTypes, PacketEvent.class);
    int packetTypeParameterPosition = findParameterPosition(parameterTypes, PacketType.class);

    // same assignment order as the previous array-based implementation, later assignments win
    ArgumentResolver[] resolvers = new ArgumentResolver[arity];
    Arrays.fill(resolvers, NONE);
    set(resolvers, playerParameterIndex, (event, reader) -> event.getPlayer());
    set(resolvers, userParameterPosition, (event, reader) -> userLookup.apply(event.getPlayer()));
    set(resolvers, cancelableParameterPosition, (event, reader) -> event);
    set(resolvers, packetContainerParameterPosition, (event, reader) -> event.getPacket());
    set(resolvers, packetReaderParameterPosition, (event, reader) -> reader);
    set(resolvers, packetEventParameterPosition, (event, reader) -> event);
    set(resolvers, packetTypeParameterPosition, (event, reader) -> event.getPacketType());
    this.resolvers = resolvers;
    this.requiresReader = packetReaderParameterPosition != -1;
  }

  static MethodHandleSubscriptionExecutor of(Method calledMethod) {
    return new MethodHandleSubscriptionExecutor(
      calledMethod, UserRepository::userOf, PacketReaders::readerOf,
      message -> IntaveLogger.logger().info(message)
    );
  }

  static MethodHandleSubscriptionExecutor of(
    Method calledMethod,
    Function<? super Player, ? extends User> userLookup,
    Function<? super PacketContainer, ? extends PacketReader> readerFactory,
    Consumer<String> skipLog
  ) {
    return new MethodHandleSubscriptionExecutor(calledMethod, userLookup, readerFactory, skipLog);
  }

  private static void set(ArgumentResolver[] resolvers, int position, ArgumentResolver resolver) {
    if (position != -1) {
      resolvers[position] = resolver;
    }
  }

  private static MethodHandle handleOf(Method method) {
    try {
      method.setAccessible(true);
      MethodHandle handle = MethodHandles.lookup().unreflect(method);
      return handle.asType(MethodType.genericMethodType(method.getParameterCount() + 1).changeReturnType(void.class));
    } catch (IllegalAccessException exception) {
      throw new IllegalStateException("Unable to access packet subscription method " + method, exception);
    }
  }

  @Override
  public void invoke(PacketEventSubscriber subscriber, PacketEvent event) {
    if (block) {
      return;
    }
    PacketReader packetReader = null;
    if (requiresReader) {
      try {
        packetReader = readerFactory.apply(event.getPacket());
      } catch (Exception e) {
        block = true;
        skipLog.accept(subscriber.getClass().getCanonicalName() + " skipped packet type due to ProtocolLib missing packet " + event.getPacketType().name());
        return;
      }
    }
    try {
      ArgumentResolver[] resolvers = this.resolvers;
      switch (arity) {
        case 0:
          handle.invokeExact((Object) subscriber);
          break;
        case 1:
          handle.invokeExact((Object) subscriber, resolvers[0].resolve(event, packetReader));
          break;
        case 2:
          handle.invokeExact((Object) subscriber, resolvers[0].resolve(event, packetReader), resolvers[1].resolve(event, packetReader));
          break;
        case 3:
          handle.invokeExact((Object) subscriber, resolvers[0].resolve(event, packetReader), resolvers[1].resolve(event, packetReader),
            resolvers[2].resolve(event, packetReader));
          break;
        case 4:
          handle.invokeExact((Object) subscriber, resolvers[0].resolve(event, packetReader), resolvers[1].resolve(event, packetReader),
            resolvers[2].resolve(event, packetReader), resolvers[3].resolve(event, packetReader));
          break;
        case 5:
          handle.invokeExact((Object) subscriber, resolvers[0].resolve(event, packetReader), resolvers[1].resolve(event, packetReader),
            resolvers[2].resolve(event, packetReader), resolvers[3].resolve(event, packetReader), resolvers[4].resolve(event, packetReader));
          break;
        default:
          Object[] arguments = new Object[arity + 1];
          arguments[0] = subscriber;
          for (int i = 0; i < arity; i++) {
            arguments[i + 1] = resolvers[i].resolve(event, packetReader);
          }
          handle.invokeWithArguments(arguments);
          break;
      }
    } catch (Throwable throwable) {
      throw new RuntimeException("Failed to invoke packet subscription method " + calledMethod + " in " + subscriber.getClass().getCanonicalName(), throwable);
    } finally {
      if (packetReader != null) {
        packetReader.releaseSafe();
      }
    }
  }

  private static int findParameterPosition(Class<?>[] parameterTypes, Class<?> parameterType) {
    for (int i = 0; i < parameterTypes.length; i++) {
      if (parameterTypes[i] == parameterType) {
        return i;
      }
      // or is a subclass of the parameter type
      if (parameterType.isAssignableFrom(parameterTypes[i])) {
        return i;
      }
    }
    return -1;
  }

  @FunctionalInterface
  private interface ArgumentResolver {
    Object resolve(PacketEvent event, PacketReader reader);
  }
}
