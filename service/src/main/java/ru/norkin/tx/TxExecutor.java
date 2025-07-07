package ru.norkin.tx;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.transaction.RollbackException;

public class TxExecutor {
    private final EntityManagerFactory emf;

    public TxExecutor(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public <T> T callInNewTx(TxCallable<T> callable) throws Exception {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T result = callable.call(em);
            if (tx.getRollbackOnly()) {
                //For simpler semantics like "rollback = exception" we throw exception here.
                //It isn't thrown automatically from tx.commit() if rollbackOnly is set.
                throw new RollbackException("Transaction was marked for rollback");
            }
            else {
                tx.commit();
                return result;
            }
        }
        catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.setRollbackOnly();
            }
            throw e;
        }
        finally {
            try {
                if (tx.isActive()) {
                    //We can only get here in case of exception
                    tx.rollback();
                }
            }
            finally {
                //If the DB became unreachable during tx.rollback() call, then a new
                //exception would be thrown and it's important we close the EntityManager
                //to avoid leaking connections.
                em.close();
            }
        }
    }
}

