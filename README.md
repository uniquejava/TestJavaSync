JUnit测试多线程有两个问题:

1. main thread结束后，　junit会直接结束jvm，不会等待sub thread执行完毕.
2. 即使使用了thread.join(), 在thread中assert失败时，junit也会认为测试成功。