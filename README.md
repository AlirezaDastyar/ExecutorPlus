# ExecutorPlus ![Build status](https://img.shields.io/teamcity/codebetter/bt428.svg)

## Introduction
A simple implementation of java Executor interface which comes with more information and access on threads state than ExecutorService.
> Note: This project is in alpha release so if care enough to test and feedback its apretiatated.

## Some use cases
Imagin you are writing a multi-threaded file system search which
looked like this:

```java
import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import space.dastyar.lib.executorplus.ExecutorPlus;
import space.dastyar.lib.executorplus.ExecutorPlusFactory;

/**
* The way program works is that the
* searcher obeject start listing files in
* the given path and if its a match file adds
* it to the filePaths and if its a directory
* create new search task and subimt it to the execute
* method of Executor object.
*/
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
In the above program there is a lot of similarity between `ExecutorPlus` and `ExecutorService` but one of key futures <br/>of
`ExecutorPlus` API is `waitToFinish()` mentioned by `**** this line ****` comment.<br/>
Yes there is something like that in `ExecutorService` called `awaitTermination()` but this method requiers a thread<br/> to call the `shutdown()` on `ExecutorService` object.<br/> In the above case how you could know when to call `shutdown()` ? of course there are other ways around ExecutorService. <br/>
The main point is that `ExecutorPlus` provide more information about threads internal state.<br/>
Read about these futures in `ExecutorPlus` [API documentation](https://github.com/AlirezaDastyar/ExecutorPlus/blob/master/src/main/java/space/dastyar/lib/executorplus/ExecutorPlus.java).
