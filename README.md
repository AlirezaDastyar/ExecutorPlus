# ExecutorPlus ![Build status](https://img.shields.io/teamcity/codebetter/bt428.svg)

## Introduction.
A simple implementation of Java `Executor` interface which comes with more information and access on threads state than `ExecutorService`.
> Note: This project is in alpha release so if you're interested, try to test and give some feedback.

## What does it offer?
It offers almost all of the functionalities of `ExecutorService` and **more**.  
Extra information comes from the internal state of threads which is achieved by using customized threads, so you won't be able use your own thread factory, instead you have access to more information.
#### What are those extra information? 
Extra information is about whether threads are busy or not.  
Since you might not know exact number tasks or one may depend on
one or more number of tasks, you won't be able to know whether 
the submitted tasks are done or not, by using `ExecutorPlus` you would be able to
know if `Executor` has done all of the submitted task, or block and wait for `Executor` to finish.  
> Note: accessing to all of the extra information requires no extra operation such as `shutdown()` and Executor will be  usable until you want to.  

## Some use cases.
Imagine you are writing a multi-threaded file system search which consider each sub-directory as a new task and submit tasks to an Executor 
and each match case will be stored in a list, is this case how could you know how many tasks are you dealing with? or when are all of them done?

Our search code could looked like this:

```java
import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import space.dastyar.lib.executorplus.ExecutorPlus;
import space.dastyar.lib.executorplus.ExecutorPlusFactory;


public class FileSearcher implements Runnable {

    private Executor executor;
    private String path;
    private String search;
    private List<String> filePaths;

    public FileSearcher(Executor executor) {
        this.executor = executor;
        filePaths = new CopyOnWriteArrayList<>();
    }

    private FileSearcher(Executor executor, String path,
        String search, List<String> filesPath) {
        this.executor = executor;
        this.path = path;
        this.search = search;
        this.filePaths = filesPath;
    }
	
    /**
    * Search method searches between files and deal with sub-dirs 
    * like a recursive search but insted of calling it self again, 
    * creates a task and submit the to the executor.
    * so each sub-dir to search will be a task for excutor to search.
	* 
    * @param path main directory to search
    * @param search the file name to search 
    **/
    public void search(String path, String search) {
        this.search = search;
        File[] files = new File(path).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String name = file.getName();
                    if (name.toLowerCase().contains(search.toLowerCase())) {
                        filePaths.add(file.getPath());
                    }
                } else {
                    executor.execute(
                          new FileSearcher(executor, file.getPath(),
                            search, filePaths));
                }
            }
        }
    }

    public List<String> getResult() {
        return filePaths;
    }

    @Override
    public void run() {
        search(path, search);
    }

    public static void main(String[] args) {
        //ExecutorService executor=Executors.newFixedThreadPool(4);
        ExecutorPlus executor = ExecutorPlusFactory.newFixedThreadPool(4);
        FileSearcher searcher = new FileSearcher(executor);
        searcher.search("/home/me/MyFolder", "game");
        System.out.println("Searching...");
        //******  this line ******
        executor.waitToFinish();
        List<String> paths = searcher.getResult();
        if (!paths.isEmpty()) {
            for (String path : paths) {
                System.out.println(path);
            }
        } else {
            System.out.println("Not Found");
        }
        System.out.println("Finish!");
    }
}

```
#### How does the `ExecutorPlus` helps?
In the above program there is a lot of similarity between `ExecutorPlus` and `ExecutorService` but one of key futures <br/>of
`ExecutorPlus` API is `waitToFinish()` method which is marked by `**** this line ****` comment.<br/>
Yes there is something like that in `ExecutorService` called `awaitTermination()` but this method requires a thread<br/> to call the `shutdown()` on `ExecutorService` object.<br/> In the above case how you could know when to call `shutdown()` ? or what if you still need the `Executor`? of course there are other ways around `ExecutorService`. always there is a way but does is worth the complexity? <br/>
`ExecutorPlus` offers a lot at low cost and the main future is that it provide more information about threads internal state.<br/>
## Documentation.
The main interfaces of `ExecutorPlus` are well documented, and if know how to work with `ExecutorService` you are good to go but **keep in mind** that while `ExecutorPlus` and `ExecutorService` share most of their concepts, **they have different implementations so checkout the documentation before using any method or feature.**
### Main interfaces
`ExecutorPlus` is what you are going to work with (Equivalent of `ExecutorService`) [API documentation](https://github.com/AlirezaDastyar/ExecutorPlus/blob/master/src/main/java/space/dastyar/lib/executorplus/ExecutorPlus.java).  
`ExecutorPlusFactory` is main way of instantiation of `ExecutorPlus`(Equivalent of `Executors`) [API documentation](https://github.com/AlirezaDastyar/ExecutorPlus/blob/master/src/main/java/space/dastyar/lib/executorplus/ExecutorPlusFactory.java).  
