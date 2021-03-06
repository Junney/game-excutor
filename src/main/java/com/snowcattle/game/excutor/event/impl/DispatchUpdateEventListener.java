package com.snowcattle.game.excutor.event.impl;

import com.snowcattle.game.excutor.event.EventBus;
import com.snowcattle.game.excutor.event.EventParam;
import com.snowcattle.game.excutor.event.IEvent;
import com.snowcattle.game.excutor.pool.IUpdateExcutor;
import com.snowcattle.game.excutor.pool.UpdateExecutorService;
import com.snowcattle.game.excutor.service.UpdateService;
import com.snowcattle.game.excutor.thread.LockSupportDisptachThread;
import com.snowcattle.game.excutor.thread.LockSupportUpdateFuture;
import com.snowcattle.game.excutor.thread.LockSupportUpdateFutureThread;
import com.snowcattle.game.excutor.thread.listener.LockSupportUpdateFutureListener;
import com.snowcattle.game.excutor.update.IUpdate;
import com.snowcattle.game.excutor.utils.Constants;
import com.snowcattle.game.excutor.utils.Loggers;

/**
 * Created by jiangwenping on 17/1/11.
 */
public class DispatchUpdateEventListener extends UpdateEventListener {
    private LockSupportDisptachThread dispatchThread;
    private UpdateService updateService;
    public DispatchUpdateEventListener(LockSupportDisptachThread dispatchThread, UpdateService updateService) {
        this.dispatchThread = dispatchThread;
        this.updateService = updateService;
    }


    public void fireEvent(IEvent event) {
//        if(Loggers.utilLogger.isDebugEnabled()){
//            Loggers.utilLogger.debug("处理update");
//        }
        super.fireEvent(event);

        //提交执行线程
        UpdateEvent updateEvent = (UpdateEvent) event;
        EventParam[] eventParams = event.getParams();
        for(EventParam eventParam: eventParams) {
            IUpdate iUpdate = (IUpdate) eventParam.getT();
            boolean aliveFlag = updateEvent.isUpdateAliveFlag();
            if (aliveFlag) {
                IUpdateExcutor iUpdateExcutor = dispatchThread.getiUpdateExcutor();
                iUpdateExcutor.excutorUpdate(dispatchThread, iUpdate, updateEvent.isInitFlag(), updateEvent.getUpdateExcutorIndex());
            } else {
                FinishEvent finishEvent = new FinishEvent(Constants.EventTypeConstans.finishEventType, eventParams);
                dispatchThread.addFinishEvent(finishEvent);
            }
        }

    }
}
