package de.jpx3.intave.block.state;

import de.jpx3.intave.user.User;

/**
 * A block state access  merges
 * {@link BlockStateInvalidatableCache} featuring methods for type caching
 * {@link BlockStateOverridableCache}, featuring methods for type override and
 * {@link BlockStateCache} for basic lookup access.
 *
 * @see User
 * @see BlockState
 * @see BlockStateCache
 * @see BlockStateOverridableCache
 * @see BlockStateInvalidatableCache
 * @see MultiChunkKeyBlockStateExtendedCache
 */
public interface BlockStateExtendedCache extends BlockStateCache, BlockStateInvalidatableCache, BlockStateOverridableCache {
}