package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.common.RedisShardedPool;
import com.mmall.common.RedissonManager;
import com.mmall.service.IOrderService;
import com.mmall.utils.PropertiesUtil;
import com.mmall.utils.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.StandardSocketOptions;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CloseOrderTask {

    @Autowired
    private RedissonManager redissonManager;

    @Autowired
    private IOrderService iOrderService;

    //非集群
   // @Scheduled(cron = "0 */1 * * * ?")//每个1分钟的整数倍
    public void closeOrderTaskV1(){
        log.info("关闭订单定时任务启动");
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour"));
        iOrderService.closeOrder(hour);
        log.info("关闭订单定时任务结束");
    }

    //@Scheduled(cron = "0 */1 * * * ?")//每个1分钟的整数倍
    public void closeOrderTaskV2(){
        log.info("关闭订单定时任务启动");
        //50s
        long lockTimeout  = Integer.parseInt(PropertiesUtil.getProperty("lock.timeout"));
        //获取锁
        Long setnxResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
                String.valueOf(System.currentTimeMillis() + lockTimeout));
        if(setnxResult!=null&& setnxResult.intValue()==1){
            //返回1，获取到了锁
            //不要直接调用closeOrder,第一次启动任务锁没有设置有效期，以后执行一定获取不到锁
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);

        }else{
            log.info("没有获取到分布式锁：{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }

        log.info("关闭订单定时任务结束");
    }

    private void closeOrder(String lockName){
        RedisShardedPoolUtil.expire(lockName,5);//有效期50s，防止死锁
        log.info("获取：{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour"));
        iOrderService.closeOrder(hour);
        //及时释放锁
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        log.info("释放：{},ThreadName：{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        log.info("=============================================");

    }

    @PreDestroy //关闭容器前执行，时间可能很多，直接kill进程不会执行
    public void delLock(){
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
    }


    //@Scheduled(cron = "0 */1 * * * ?")//每个1分钟的整数倍
    public void closeOrderTaskV3(){
        log.info("关闭订单定时任务启动");
        //50s
        long lockTimeout  = Integer.parseInt(PropertiesUtil.getProperty("lock.timeout"));
        //获取锁
        Long setnxResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
                String.valueOf(System.currentTimeMillis() + lockTimeout));
        if(setnxResult!=null&& setnxResult.intValue()==1){
            //返回1，获取到了锁
            //不要直接调用closeOrder,第一次启动任务锁没有设置有效期，以后执行一定获取不到锁
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);

        }else{
            //未获取到锁（其他线程在使用锁，没有过expireTime,没有被删除del/异常导致锁一直在数据库没有释放），是否可以重置并获取到锁
            String lockValueStr= RedisShardedPoolUtil.get(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            if(lockValueStr !=null && System.currentTimeMillis()>Long.parseLong(lockValueStr)){
                //锁存在，即时没有设置过expire，但是value超过了当前时间，说明锁实际上已经过期了
                //多个tomcat,lockValStr可能与现在获取的旧值getSetStr不一样（被别的进程先一步修改了）
                String getSetStr  = RedisShardedPoolUtil.getSet(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis() + lockTimeout));
                if(getSetStr==null || getSetStr!=null&&lockValueStr.equals(getSetStr)){
                    //1.getset前线程释放了锁,getSetStr已经超时（原本没有超时）
                    //2.锁没有被别的线程获取，并且value表明锁已经过期
                    //？？？？有问题：本线程设置了锁，并且getSet的值不是原值，被其他线程捷足先登，这样我就是set了别人的锁
                    //获取锁成功
                    closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                }else{
                    log.info("没有获取到分布式锁：{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                }
            }else {
                log.info("没有获取到分布式锁：{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            }
        }

        log.info("关闭订单定时任务结束");
    }

    /**
     * Redisson实现
     */
    @Scheduled(cron = "0 */1 * * * ?")//每个1分钟的整数倍
    public void closeOrderTaskV4(){
        RLock lock = redissonManager.getRedisson().getLock(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        Boolean getLock = false;
        try {
            //等待锁时间，获得被动锁后释放的时间，单位
            if(getLock = lock.tryLock(0, 500, TimeUnit.SECONDS)){
                log.info("Redisson获取分布式锁:{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
                int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour"));
                 iOrderService.closeOrder(hour);
            }else{
                log.info("Redisson没有获取分布式锁:{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            log.error("Redisson分布式锁获取异常",e);
        }finally {
            if(!getLock){
                //没有拿到
                return ;
            }
            //拿到释放
            lock.unlock();
            log.info("Redisson分布式锁释放");
        }
    }

}
