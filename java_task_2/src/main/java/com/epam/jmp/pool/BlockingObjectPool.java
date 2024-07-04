package com.epam.jmp.pool;

/**
 * Pool that block when it has not any items, or it fulls

 */
public interface BlockingObjectPool {
    /**
     * Gets object from pool or blocks if pool is empty
     *
     * @return object from pool
     */
    Object get();

    /**
     * Puts object to pool or blocks if pool is full
     *
     * @param object to be taken back to pool
     */
    void take(Object object);

    /**
     * Returns current pool size
     *
     * @return pool size
     */
    int size();
}
