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
import de.jpx3.intave.packet.reader.PacketReader;
import de.jpx3.intave.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

final class MethodHandleSubscriptionExecutorTest {
  // PacketType constants need a running server, so use a bare instance with only its name set
  private static final PacketType TYPE = allocate(PacketType.class, "name", "ENTITY_VELOCITY");

  private final Player player = proxy(Player.class);
  private final User user = proxy(User.class);
  // PacketContainer can't be initialized without a server either; the container is passed through untouched, so null stands in for it
  private final PacketContainer packet = null;
  private final PacketEvent event = new TestEvent(this, player);
  private final CountingReader reader = new CountingReader();
  private final List<String> logs = new ArrayList<>();
  private final AtomicInteger userLookups = new AtomicInteger();
  private final AtomicInteger readerCreations = new AtomicInteger();

  @Test
  void resolvesEverySupportedParameterType() {
    Subscriber subscriber = new Subscriber();
    execute(subscriber, "all");
    assertEquals(Arrays.asList(player, user, event, packet, reader, event, TYPE), subscriber.arguments);
    assertEquals(1, userLookups.get());
    assertEquals(1, readerCreations.get());
    assertEquals(1, reader.released);
  }

  @Test
  void resolvesSmallerCombinations() {
    Subscriber subscriber = new Subscriber();
    execute(subscriber, "none");
    assertEquals(Arrays.asList(), subscriber.arguments);
    execute(subscriber, "player");
    assertEquals(Arrays.asList(player), subscriber.arguments);
    execute(subscriber, "userReader");
    assertEquals(Arrays.asList(user, reader), subscriber.arguments);
    execute(subscriber, "typeEventCancellable");
    // PacketEvent is the first Cancellable-compatible position, so the trailing Cancellable stays unresolved (unchanged behavior)
    assertEquals(Arrays.asList(TYPE, event, null), subscriber.arguments);
    execute(subscriber, "five");
    assertEquals(Arrays.asList(player, user, packet, reader, TYPE), subscriber.arguments);
    assertEquals(0, readerCreations.get() - reader.released);
  }

  @Test
  void duplicateParameterTypesOnlyFillTheFirstPosition() {
    Subscriber subscriber = new Subscriber();
    execute(subscriber, "twoPlayers");
    assertEquals(Arrays.asList(player, null), subscriber.arguments);
  }

  @Test
  void readerFailureBlocksFurtherInvocationsAndLogs() {
    Subscriber subscriber = new Subscriber();
    MethodHandleSubscriptionExecutor executor = MethodHandleSubscriptionExecutor.of(
      method("userReader"), p -> user, p -> { throw new IllegalStateException("missing"); }, logs::add
    );
    executor.invoke(subscriber, event);
    executor.invoke(subscriber, event);
    assertNull(subscriber.arguments);
    assertEquals(1, logs.size());
    assertEquals(Subscriber.class.getCanonicalName() + " skipped packet type due to ProtocolLib missing packet ENTITY_VELOCITY", logs.get(0));
  }

  @Test
  void handlerExceptionsAreWrappedAndReaderIsReleased() {
    Subscriber subscriber = new Subscriber();
    Method method = method("throwing");
    RuntimeException exception = assertThrows(RuntimeException.class, () -> executor(method).invoke(subscriber, event));
    assertEquals("Failed to invoke packet subscription method " + method + " in " + Subscriber.class.getCanonicalName(), exception.getMessage());
    assertInstanceOf(UnsupportedOperationException.class, exception.getCause());
    assertEquals(1, reader.released);
  }

  private void execute(Subscriber subscriber, String name) {
    executor(method(name)).invoke(subscriber, event);
  }

  private MethodHandleSubscriptionExecutor executor(Method method) {
    return MethodHandleSubscriptionExecutor.of(method, p -> {
      assertSame(player, p);
      userLookups.incrementAndGet();
      return user;
    }, container -> {
      assertSame(packet, container);
      readerCreations.incrementAndGet();
      return reader;
    }, logs::add);
  }

  private static Method method(String name) {
    return Arrays.stream(Subscriber.class.getMethods())
      .filter(method -> method.getName().equals(name))
      .findFirst().orElseThrow(AssertionError::new);
  }

  private static <T> T allocate(Class<T> type, String fieldName, Object value) {
    try {
      Field unsafeField = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
      unsafeField.setAccessible(true);
      Object unsafe = unsafeField.get(null);
      T instance = type.cast(unsafe.getClass().getMethod("allocateInstance", Class.class).invoke(unsafe, type));
      Field field = type.getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(instance, value);
      return instance;
    } catch (ReflectiveOperationException exception) {
      throw new AssertionError(exception);
    }
  }

  private static <T> T proxy(Class<T> type) {
    return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (proxy, method, args) -> {
      switch (method.getName()) {
        case "equals":
          return proxy == args[0];
        case "hashCode":
          return System.identityHashCode(proxy);
        case "toString":
          return type.getSimpleName() + "Proxy";
        default:
          throw new AssertionError("Unexpected call: " + method);
      }
    }));
  }

  private static final class TestEvent extends PacketEvent {
    private final Player player;

    private TestEvent(Object source, Player player) {
      super(source);
      this.player = player;
    }

    @Override
    public Player getPlayer() {
      return player;
    }

    @Override
    public PacketContainer getPacket() {
      return null;
    }

    @Override
    public PacketType getPacketType() {
      return TYPE;
    }
  }

  private static final class CountingReader implements PacketReader {
    private int released;

    @Override
    public void enter(PacketContainer packet) {
    }

    @Override
    public void flush() {
    }

    @Override
    public void release() {
      released++;
    }

    @Override
    public void releaseSafe() {
      released++;
    }
  }

  public static final class Subscriber implements PacketEventSubscriber {
    private List<Object> arguments;

    public void all(Player player, User user, Cancellable cancellable, PacketContainer packet, PacketReader reader, PacketEvent event, PacketType type) {
      arguments = Arrays.asList(player, user, cancellable, packet, reader, event, type);
    }

    public void none() {
      arguments = Arrays.asList();
    }

    public void player(Player player) {
      arguments = Arrays.asList(player);
    }

    public void userReader(User user, PacketReader reader) {
      arguments = Arrays.asList(user, reader);
    }

    public void typeEventCancellable(PacketType type, PacketEvent event, Cancellable cancellable) {
      arguments = Arrays.asList(type, event, cancellable);
    }

    public void five(Player player, User user, PacketContainer packet, PacketReader reader, PacketType type) {
      arguments = Arrays.asList(player, user, packet, reader, type);
    }

    public void twoPlayers(Player first, Player second) {
      arguments = Arrays.asList(first, second);
    }

    public void throwing(PacketReader reader) {
      throw new UnsupportedOperationException("handler failure");
    }
  }
}
