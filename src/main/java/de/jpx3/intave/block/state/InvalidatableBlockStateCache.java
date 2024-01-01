package de.jpx3.intave.block.state;

/**
 * The {@link InvalidatableBlockStateCache} extends the functionality of the
 * {@link BlockStateCache} by adding methods related to cache invalidation.
 *
 * @see BlockStateCache
 * @see OverridableBlockStateCache
 * @see ExtendedBlockStateCache
 */

public interface InvalidatableBlockStateCache {
  /**
   * Invalidate all caches
   */
  void invalidateAll();

  /**
   * Invalidate resolver caches
   */
  void invalidateCache();

  /**
   * Invalidate all blocks next to a specified position
   *
   * @param posX the x coordinate of the selected block
   * @param posY the y coordinate of the selected block
   * @param posZ the z coordinate of the selected block
   */
  default void invalidateCacheAround(int posX, int posY, int posZ) {
    invalidateCacheAt(posX + 1, posY, posZ);
    invalidateCacheAt(posX - 1, posY, posZ);
    invalidateCacheAt(posX, posY, posZ + 1);
    invalidateCacheAt(posX, posY, posZ - 1);
    invalidateCacheAt(posX, posY + 1, posZ);
    invalidateCacheAt(posX, posY - 1, posZ);
    invalidateCacheAt(posX, posY, posZ);
  }

  /**
   * Invalidate a specific block
   *
   * @param posX the x coordinate of the selected block
   * @param posY the y coordinate of the selected block
   * @param posZ the z coordinate of the selected block
   */
  void invalidateCacheAt(int posX, int posY, int posZ);
}
