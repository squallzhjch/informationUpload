package com.informationUpload.thread;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * @ClassName: ThreadManager.java
 * @author zhjch
 * @version V1.0
 * @Date 2015年10月30日 上午9:41:32
 * @Description: TODO(用于处理后台读存数据管理等)
 */
public class ThreadManager {

	
	public static abstract class OnDatabaseOperationRunnable<T> implements Runnable {

		private static final Handler UI_HANDLER = new Handler(Looper.getMainLooper());
		private final UiRunnableImpl<T> mUiRunnable;
		private static boolean cancel = false;
		public abstract T doInBackground();

		public abstract void onSuccess(T value);

		public void onOperationFailed(Exception e){
		}
		
		public void cancel(){
			cancel = true;
		}
		public OnDatabaseOperationRunnable() {
			mUiRunnable = new UiRunnableImpl<T>(this);
		}

		public void run() {
			try {
				final T value = doInBackground();
				mUiRunnable.setValue(value);
				if(!cancel){
					UI_HANDLER.post(mUiRunnable);
				}
			} catch (Exception e) {
				e.printStackTrace();
				onOperationFailed(e);
			}
		}

		private static class UiRunnableImpl<T> implements Runnable {

			private T mValue;
			private OnDatabaseOperationRunnable mDbRunnable;

			UiRunnableImpl(OnDatabaseOperationRunnable<T> dbRunnable) {
				mDbRunnable = dbRunnable;
			}

			public void setValue(T value) {
				mValue = value;
			}

			@Override
			public void run() {
				if(!cancel)
					mDbRunnable.onSuccess(mValue);
			}
		};
	}
	
	
	private Context mContext;
	private static volatile ThreadManager mInstance;
	private DatabaseHandlerThread mMessageHandlerThread;
	private ThreadManager() {
		
	}

	public static ThreadManager getInstance() {
		if (mInstance == null) {
			synchronized (ThreadManager.class) {
				if (mInstance == null) {
					mInstance = new ThreadManager();
				}
			}
		}
		return mInstance;
	}
	
	public void init(Context context) {
        mContext = context;
        mMessageHandlerThread = new DatabaseHandlerThread(mContext);
        mMessageHandlerThread.setName("Database_OP");
        mMessageHandlerThread.setDaemon(true);
        mMessageHandlerThread.setPriority(1);
        mMessageHandlerThread.start();

        synchronized (mMessageHandlerThread) {
            while (mMessageHandlerThread.getLooper() == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public Handler getHandler() {
        return mMessageHandlerThread.getHandler();
    }
    
    private class DatabaseHandlerThread extends Thread {

        private LooperHandler mLooperHandler;
        private Looper mLooper;
        private final Context mContext;

        public DatabaseHandlerThread(Context context) {
            mContext = context;
        }

        public void run() {
            Looper.prepare();
            mLooperHandler = new LooperHandler(mContext);
            synchronized (this) {
                mLooper = Looper.myLooper();
                notifyAll();
            }
            Looper.loop();
        }

        public Looper getLooper() {
            synchronized (this) {
                while (mLooper == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
            return mLooper;
        }

        public Handler getHandler() {
            return mLooperHandler;
        }
    }
    
    private class LooperHandler extends Handler {
        
    	final Context mContext;
        
        public LooperHandler(Context context) {
            mContext = context;
        }
        
        @Override
        public void handleMessage(Message message) {
            message.getCallback();
        }
    }
}
