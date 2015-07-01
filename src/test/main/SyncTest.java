import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.number.OrderingComparison.greaterThan;

/**
 * 测试Java synchronized关键字.
 */
public class SyncTest {

    @Test
    public void testLockClassAndInvokeStaticMethodOnIt() throws InterruptedException {
        Thread t1 = createThreadToLock(Fuck.class);


        //新开的第２个线程
        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                long time1 = System.currentTimeMillis();

                //尝试使用被线程1锁住的资源
                //等待线程1释放该资源
                System.out.println("Try to call fuck.you()");
                Fuck.you();
                System.out.println("Fuck.you done.");

                long time2 = System.currentTimeMillis();

                //执行时间至少需要超过１秒才正确
                System.out.println(time2 - time1);

                Assert.assertThat(time2 - time1, greaterThan(1000L));
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();


    }

    @Test
    public void testLockClassAndInvokeNonStaticMethodOnIt() throws InterruptedException {
        Thread t1 = createThreadToLock(Fuck.class);

        //新开的第２个线程
        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                long time1 = System.currentTimeMillis();

                //尝试使用被线程1锁住的资源
                //结果是可以直接调用非静态方法。
                System.out.println("creating fuck object.");
                Fuck fuck = new Fuck();
                System.out.println("fuck obj created.");

                System.out.println("calling fuck.nonStaticMethod()");
                fuck.nonStaticMethod();
                System.out.println("Fuck.nonStaticMethod done.");


                long time2 = System.currentTimeMillis();

                System.out.println(time2 - time1);
                //断言失败
                Assert.assertThat(time2 - time1, greaterThan(1000L));
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();


    }

    private Thread createThreadToLock(final Object obj) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                //在这一个类上加锁，结果是锁住了这个类的其它static并且加了synchronized关键的方法。
                //此锁对没有加sync关键字的和没加static关键字的方法不起作用
                synchronized (obj) {

                    //占有该资源５秒
                    System.out.println("Locking fuck");
                    //一旦占有锁后，可以直接访问该资源
                    System.out.println(obj);

                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //释放该资源
                    System.out.println("Unlocking fuck");
                }
            }
        });
    }

    @Test
    public void testLockProperty() throws InterruptedException {
        //开两个线程，竞争资源.
        Thread t1 = createThreadToLock(Fuck.YEAR_LIST);

        //新开的第２个线程
        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                long t1 = System.currentTimeMillis();

                //尝试使用被线程1锁住的资源
                System.out.println("Try to read YEAR_LIST");

                //等等线程1释放该资源
                System.out.println(Fuck.YEAR_LIST);

                System.out.println("read YEAR_LIST complete.");

                long t2 = System.currentTimeMillis();

                //执行时间至少需要超过１秒才正确
                System.out.println(t2 - t1);

                Assert.assertThat(t2 - t1, greaterThan(1000L));
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();


    }

}

class Fuck {
    //http://stackoverflow.com/questions/6910807/synchronization-of-non-final-field
    //http://stackoverflow.com/questions/22966382/java-synchronization-on-non-final-field
    public static final List<String> YEAR_LIST = Collections.synchronizedList(new ArrayList<String>());

    public static synchronized void you() {
        System.out.println("fuck.you() is called.");
    }

    public synchronized void nonStaticMethod() {
        System.out.println("fuck.nonStaticMethod() is called.");
    }
}