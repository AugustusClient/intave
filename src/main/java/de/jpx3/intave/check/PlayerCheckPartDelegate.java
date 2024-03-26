package de.jpx3.intave.check;

import de.jpx3.intave.module.linker.bukkit.BukkitEventSubscriber;
import de.jpx3.intave.module.linker.bukkit.PlayerBukkitEventSubscriber;
import de.jpx3.intave.module.linker.packet.PacketEventSubscriber;
import de.jpx3.intave.module.linker.packet.PlayerPacketEventSubscriber;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.CheckCustomMetadata;

import java.util.function.Function;

class PlayerCheckPartDelegate<P extends Check, D extends PlayerCheckPart<P>>
  extends MetaCheckPart<P, PlayerCheckPartDelegate.DelegateMeta>
  implements PlayerBukkitEventSubscriber, PlayerPacketEventSubscriber
{
  private final Function<? super User, ? extends D> generator;

  protected PlayerCheckPartDelegate(P parentCheck, Class<? extends D> delegateClass) {
    this(parentCheck, user -> {
      try {
        return delegateClass.getDeclaredConstructor(User.class, parentCheck.getClass()).newInstance(user, parentCheck);
      } catch (ReflectiveOperationException e) {
        throw new RuntimeException(e);
      }
    });
  }

  protected PlayerCheckPartDelegate(P parentCheck, Function<? super User, ? extends D> generator) {
    super(parentCheck, DelegateMeta.class);
    this.generator = generator;
  }

  @Override
  public PacketEventSubscriber packetSubscriberFor(User user) {
    return delegateOf(user);
  }

  @Override
  public BukkitEventSubscriber bukkitSubscriberFor(User user) {
    return delegateOf(user);
  }

  private D delegateOf(User user) {
    //noinspection unchecked
    D delegate = (D) metaOf(user).delegate;
    if (delegate == null) {
      delegate = generator.apply(user);
      metaOf(user).delegate = delegate;
    }
    return delegate;
  }

  public static class DelegateMeta extends CheckCustomMetadata {
    private Object delegate;
  }
}
