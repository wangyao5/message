package com.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Component
public class ZkOperator {
    private ZooKeeper zk;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public void initZK() {
        try {
            zk = new ZooKeeper("127.0.0.1:2181", 300000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    System.out.println("lianjie watched");
                }
            });
        } catch (IOException e) {
            initZK();
        }
    }

    public void createEphemeral(String znode) {
        try {
            zk.create(znode, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createPersistent(String znode) {
        try {
            zk.create(znode, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deleteEphemeral(String znode) {
        try {
            zk.delete(znode, -1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    public boolean exist(String znode) {
        boolean ret = false;
        try {
            Stat stat = zk.exists(znode, null);
            if (null != stat){
                ret = true;
            }
            countDownLatch.countDown();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
