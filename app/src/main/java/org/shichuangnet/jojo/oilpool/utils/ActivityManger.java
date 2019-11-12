package org.shichuangnet.jojo.oilpool.utils;

import android.app.Activity;

import java.util.Stack;

/**
 * @author jojo
 */
public class ActivityManger {

    private static Stack<Activity> activityStack;
    private static ActivityManger instance;

    public static ActivityManger getActivityManager() {
        if(instance==null) {
            instance=new ActivityManger();
        }
        return instance;
    }

    //退出栈顶Activity
    public void popActivity(Activity activity) {
        if(activity!=null){
            activity.finish();
            activityStack.remove(activity);
            activity=null;
        }
    }

    //获得当前栈顶Activity
    public Activity currentActivity(){
        Activity activity=activityStack.lastElement();
        return activity;
    }

    //将当前Activity推入栈中
    public void pushActivity(Activity activity){
        if(activityStack==null){
            activityStack=new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    //退出栈中所有Activity
    public void popAllActivityExceptOne(Class cls){
        while(true){
            Activity activity=currentActivity();
            if(activity==null){
                break;
            }
            if(activity.getClass().equals(cls) ){
                break;
            }
            popActivity(activity);
        }
    }
}
