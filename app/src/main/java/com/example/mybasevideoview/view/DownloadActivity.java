package com.example.mybasevideoview.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;

import com.arialyy.annotations.Download;
import com.arialyy.annotations.DownloadGroup;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.common.AbsEntity;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.inf.IEntity;
import com.arialyy.aria.core.task.DownloadGroupTask;
import com.arialyy.aria.core.task.DownloadTask;
import com.arialyy.aria.core.wrapper.ITaskWrapper;
import com.arialyy.aria.util.ALog;
import com.arialyy.aria.util.CommonUtil;
import com.example.mybasevideoview.R;
import com.example.mybasevideoview.model.FileDownloadMsg;
import com.example.mybasevideoview.model.RequestCode;
import com.example.mybasevideoview.model.SharedPreferenceUtil;
import com.example.mybasevideoview.utils.XslUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.security.AccessController.getContext;

public class DownloadActivity extends Activity {
    private static final String TAG = "download";
    private List<AbsEntity> mData = new ArrayList<>();

    @BindView(R.id.bar)
    SeekBar bar;
    @BindView(R.id.entry_btn)
    Button entryBtn;

    @BindView(R.id.back_btn)
    Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_download);
        XslUtils.hideStausbar(new WeakReference<>(this), true);
        ButterKnife.bind(this);

        bar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //直接消费掉触摸事件
                return true;
            }
        });

        bar.setMax(100);
        startDownload();
    }

    void startDownload() {
        Aria.download(this).register();
        setTitle("下载列表");
        List<AbsEntity> temps = Aria.download(this).getTotalTaskList();
        if (temps != null && !temps.isEmpty()) {
            for (AbsEntity temp : temps) {
                ALog.d(TAG, "AbsEntity getState = " + temp.getState());
            }
            mData.addAll(temps);
        }

        Log.d(TAG, "mData.size:" + mData.size());
        if (mData.size() > 0) {
            Log.d(TAG, "old download process: " + mData.get(0).getCurrentProgress());
        }
        if (mData.size() == 0) {
            FileDownloadMsg downloadMsg = new FileDownloadMsg(this);
            Log.d(TAG,"url:" + downloadMsg.getUrls().get(0) +
                    " names:"+downloadMsg.getNames() + " path:" + downloadMsg.getDownloadPath()
                    + " aliasName:" + downloadMsg.getAliasName());

            long taskId = Aria.download(getContext())
                    .loadGroup(downloadMsg.getUrls())
                    .setSubFileName(downloadMsg.getNames())
                    .setDirPath(downloadMsg.getDownloadPath())
                    .setGroupAlias(downloadMsg.getAliasName())
                    .unknownSize()
                    .create();

            Log.d("download", "start taskId:" + taskId);
        } else {
            Log.d(TAG, "size: " + mData.size() + " resume download :" + mData.get(0).getId() + " type:" + mData.get(0).getTaskType());
            for (int i=0; i!=mData.size(); i++) {
                Log.d(TAG, "size: " + mData.size() + " resume download :" + mData.get(i).getId() + " type:" + mData.get(i).getTaskType());
                if (mData.get(i).getState() == IEntity.STATE_COMPLETE) {
                    cancel(mData.get(i));
                } else if (mData.get(i).getState() == IEntity.STATE_RUNNING){
                    Log.d(TAG, "downloading ....");
                } else {
                    Log.d(TAG, "resume download");
                    resume(mData.get(i));
                }
            }
        }
    }

    private void resume(AbsEntity entity) {
        switch (entity.getTaskType()) {
            case ITaskWrapper.D_FTP:
                //Aria.download(getContext()).loadFtp((DownloadEntity) entity).login("lao", "123456").create();
                Aria.download(getContext()).loadFtp(entity.getId()).resume(true);
                break;
            case ITaskWrapper.D_FTP_DIR:
                Aria.download(getContext()).loadFtpDir(entity.getId()).resume(true);
                break;
            case ITaskWrapper.D_HTTP:
            case ITaskWrapper.M3U8_VOD:
                Aria.download(getContext()).load(entity.getId()).resume(true);
                break;
            case ITaskWrapper.DG_HTTP:
                Aria.download(getContext()).loadGroup(entity.getId()).resume(true);
                break;
        }
    }

    private void cancel(AbsEntity entity) {
        if (entity == null)
            return;
        switch (entity.getTaskType()) {
            case ITaskWrapper.D_FTP:
                Aria.download(getContext())
                        .loadFtp(entity.getId())
                        //.login("lao", "123456")
                        .cancel(true);
                break;
            case ITaskWrapper.D_FTP_DIR:
                break;
            case ITaskWrapper.D_HTTP:
            case ITaskWrapper.M3U8_VOD:
                Aria.download(getContext()).load(entity.getId()).cancel(true);
                break;
            case ITaskWrapper.DG_HTTP:
                Aria.download(getContext()).loadGroup(entity.getId()).cancel(true);
                break;
        }
    }

    @DownloadGroup.onWait void taskWait(DownloadGroupTask task) {
        Log.d(TAG, task.getTaskName() + "wait");
    }

    @DownloadGroup.onPre() protected void onPre(DownloadGroupTask task) {
        Log.d(TAG, "group pre");
    }

    @DownloadGroup.onTaskPre() protected void onTaskPre(DownloadGroupTask task) {
        Log.d(TAG, "group task pre");
//        if (mChildList.getSubData().size() <= 0) {
//            mChildList.addData(task.getEntity().getSubEntities());
//        }
    }

    @DownloadGroup.onTaskStart() void taskStart(DownloadGroupTask task) {
        //getBinding().setFileSize(task.getConvertFileSize());
        Log.d(TAG, "group task create");
    }

    @DownloadGroup.onTaskRunning() protected void running(DownloadGroupTask task) {
        Log.d(TAG, "group running, p = "
                + task.getPercent()
                + ", speed = "
                + task.getConvertSpeed()
                + "current_p = "
                + task.getCurrentProgress());
        bar.setProgress(task.getPercent());
//        getBinding().setProgress(task.getPercent());
//        getBinding().setSpeed(task.getConvertSpeed());
//        //Log.d(TAG, "sub_len = " + task.getEntity().getSubEntities().size());
//        mChildList.updateChildProgress(task.getEntity().getSubEntities());
    }

    @DownloadGroup.onTaskResume() void taskResume(DownloadGroupTask task) {
        Log.d(TAG, "group task resume");
    }

    @DownloadGroup.onTaskStop() void taskStop(DownloadGroupTask task) {
        Log.d(TAG, "group task stop");
//        getBinding().setSpeed("");
//        getBinding().setStateStr(getString(R.string.start));
    }

    @DownloadGroup.onTaskCancel() void taskCancel(DownloadGroupTask task) {
        Log.d(TAG, "group task cancel");
        //cancel(mData.get(0));

//        getBinding().setSpeed("");
//        getBinding().setProgress(0);
//        getBinding().setStateStr(getString(R.string.start));
        finish();
    }

    @DownloadGroup.onTaskFail() void taskFail(DownloadGroupTask task) {
        Log.d(TAG, "group task fail");
//        getBinding().setStateStr(getString(R.string.resume));
//        getBinding().setSpeed("");
    }

    @OnClick(R.id.entry_btn)
    void entryMainPlay() {
        Intent intent = new Intent();
        intent.putExtra(getResources().getString(R.string.download_result), RequestCode.MainPlay_req);
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick(R.id.back_btn)
    void back() {
        finish();
    }

    @DownloadGroup.onTaskComplete() void taskComplete(DownloadGroupTask task) {
        Log.d(TAG, "任务组下载完成");
        entryBtn.setVisibility(View.VISIBLE);
        bar.setVisibility(View.GONE);

        SharedPreferenceUtil.getInstance(this).putBoolean(getResources().getString(R.string.need_cache_view), false);
    }

    @DownloadGroup.onSubTaskRunning void onSubTaskRunning(DownloadGroupTask groupTask,
                                                          DownloadEntity subEntity) {
        //Log.e(TAG, "gHash = "
        //    + groupTask.getEntity().getSubEntities().get(0).hashCode()
        //    + "; subHash = "
        //    + groupTask.getNormalTaskWrapper().getSubTaskEntities().get(0).getEntity().hashCode() +
        //    "; subHash = " + subEntity.hashCode());
        //int percent = subEntity.getPercent();
        ////如果你打开了速度单位转换配置，将可以通过以下方法获取带单位的下载速度，如：1 mb/s
        //String convertSpeed = subEntity.getConvertSpeed();
        ////当前下载完成的进度，长度bytes
        //long completedSize = subEntity.getCurrentProgress();
        //Log.d(TAG, "subTask名字："
        //    + subEntity.getFileName()
        //    + ", "
        //    + " speed:"
        //    + convertSpeed
        //    + ",percent: "
        //    + percent
        //    + "%,  completedSize:"
        //    + completedSize);
    }

    @DownloadGroup.onSubTaskPre void onSubTaskPre(DownloadGroupTask groupTask,
                                                  DownloadEntity subEntity) {
    }

    @DownloadGroup.onSubTaskStop void onSubTaskStop(DownloadGroupTask groupTask,
                                                    DownloadEntity subEntity) {
    }

    @DownloadGroup.onSubTaskStart void onSubTaskStart(DownloadGroupTask groupTask,
                                                      DownloadEntity subEntity) {
    }

    //@DownloadGroup.onSubTaskCancel void onSubTaskCancel(DownloadGroupTask groupTask,
    //    DownloadEntity subEntity) {
    //  Log.d(TAG, "new Size: " + groupTask.getConvertFileSize());
    //  mSub.setText("子任务：" + mChildName + "，状态：取消下载");
    //}

    @DownloadGroup.onSubTaskComplete void onSubTaskComplete(DownloadGroupTask groupTask,
                                                            DownloadEntity subEntity) {
    }

    @DownloadGroup.onSubTaskFail void onSubTaskFail(DownloadGroupTask groupTask,
                                                    DownloadEntity subEntity) {
    }
}
