package com.aliyun.gts.financial.showcases.sofa.scheduler;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import com.alibaba.fastjson.JSON;
import com.alipay.antschedulerclient.common.ClientCommonResult;
import com.alipay.antschedulerclient.common.DataProcessResult;
import com.alipay.antschedulerclient.executor.data.item.IDataItem;
import com.alipay.antschedulerclient.executor.data.item.LoadData;
import com.alipay.antschedulerclient.executor.data.item.MultiDataItem;
import com.alipay.antschedulerclient.executor.data.item.SingleDataItem;
import com.alipay.antschedulerclient.executor.data.processor.IProcessor;
import com.alipay.antschedulerclient.executor.data.reader.IReader;
import com.alipay.antschedulerclient.executor.data.writer.IWriter;
import com.alipay.antschedulerclient.executor.limiter.ILimiter;
import com.alipay.antschedulerclient.handler.IClusterJobExecuteHandler;
import com.alipay.antschedulerclient.model.ClusterJobExecuteContext;
import com.alipay.antschedulerclient.model.Progress;
import com.alipay.antschedulerclient.model.chunk.RangeChunkData;
import com.aliyun.gts.financial.showcases.sofa.dao.AccountDAO;
import com.aliyun.gts.financial.showcases.sofa.facade.model.Account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

// 任务调度演示：任务执行实现
@Component
public class BatchSettleExecutor implements IClusterJobExecuteHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchSettleExecutor.class);

    @Autowired
    private AccountDAO accountDAO;

    @Override
    public String getName() {
        return "BATCH_SETTLE_EXECUTOR";
    }

    @Override
    public ThreadPoolExecutor getThreadPool() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void preExecute(ClusterJobExecuteContext context) {
        // TODO Auto-generated method stub

    }

    @Override
    public IReader getReader() {
        return new IReader() {

            @Override
            public Integer waitIntervalWhenNoData() {
                return 0;
            }

            @Override
            public LoadData<Account> read(ClusterJobExecuteContext context) throws Exception {
                RangeChunkData chunkData = (RangeChunkData) context.getChunk();
                String shard = chunkData.getShardingRule();

                // 在某个范围的数据
                List<Account> list = accountDAO.getAccountByRange(shard, Integer.valueOf(chunkData.getStart()),
                        Integer.valueOf(chunkData.getEnd()));

                LOGGER.info(String.format("BatchSettleExecutor read data.  chunkId:%s, size=%s", context.getChunkId(),
                        list.size()));
                return new LoadData<Account>(list, false);
            }
        };
    }

    @Override
    public IProcessor getProcessor() {
        return new IProcessor<Account, Account>() {

            @Override
            public DataProcessResult<Account> process(ClusterJobExecuteContext context, Account data) throws Exception {
                /**
                 * 演示：给账户余额添加1
                 */
                BigDecimal newBalance = data.getBalance().add(new BigDecimal(1));
                data.setBalance(newBalance);
                return new DataProcessResult<Account>(true, "success", data);
            }

            @Override
            public ThreadPoolExecutor getProcessThreadPool() {
                return null;
            }
        };
    }

    @Override
    public IWriter getWriter() {
        return new IWriter<Account>() {

            @Override
            public int getCountPerWrite() {
                return 1;
            }

            @Override
            public ClientCommonResult write(ClusterJobExecuteContext context, IDataItem<Account> dataItem)
                    throws Exception {
                switch (dataItem.getType()) {
                    case SINGLE:
                        LOGGER.info(String.format("getWriter write single data:%s",
                                JSON.toJSONString(((SingleDataItem<Account>) dataItem).getItem())));

                        Account account = ((SingleDataItem<Account>) dataItem).getItem();
                        Account accountToUpdate = accountDAO.getAccountForUpdate(account.getAccountNo());
                        BigDecimal newBalance = accountToUpdate.getBalance().add(new BigDecimal(1));
                        accountToUpdate.setBalance(newBalance);
                        accountDAO.updateBalance(accountToUpdate);
                        break;
                    case MULTIPLE:
                        LOGGER.info(String.format("getWriter write multi data:%s",
                                JSON.toJSONString(((MultiDataItem<Account>) dataItem).getItemList())));
                        List<Account> list = ((MultiDataItem<Account>) dataItem).getItemList();
                        if (CollectionUtils.isEmpty(list)) {
                            return ClientCommonResult.buildSuccessResult();
                        }

                        for (Account item : list) {
                            accountDAO.updateBalance(item);
                        }
                        break;
                    default:
                        break;
                }
                return ClientCommonResult.buildSuccessResult();
            }

            @Override
            public ThreadPoolExecutor getWriteThreadPool() {
                return null;
            }
        };
    }

    @Override
    public ILimiter getLimiter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void postExecute(ClusterJobExecuteContext context) {
        // TODO Auto-generated method stub

    }

    @Override
    public Progress calProgress(ClusterJobExecuteContext context) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isProcessAsync() {
        // TODO Auto-generated method stub
        return false;
    }

}