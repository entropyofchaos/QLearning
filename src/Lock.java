/**
 * A simple lock interface.
 */
public interface Lock {

   /**
    * Acquires the lock.
    */
   void lock();

   /**
    * Acquires the lock if avilable when invoked. If the
    * lock is not available, this method will not block.
    */
   boolean tryLock();

   /**
    * Releases the lock.
    */
   void unlock();

}
