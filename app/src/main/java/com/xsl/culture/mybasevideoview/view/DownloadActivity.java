package com.xsl.culture.mybasevideoview.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

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
import com.xsl.culture.R;
import com.xsl.culture.mybasevideoview.model.FileDownloadMsg;
import com.xsl.culture.mybasevideoview.model.RequestCode;
import com.xsl.culture.mybasevideoview.utils.FileUtils;
import com.xsl.culture.mybasevideoview.utils.RestoreParamMng;
import com.xsl.culture.mybasevideoview.utils.XslUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
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

    private boolean downloadFinish = false;

    @BindView(R.id.bar)
    SeekBar bar;

    @BindView(R.id.back_btn)
    Button backBtn;

    @BindView(R.id.download_btn)
    Button downloadBtn;
    @BindView(R.id.speed)
    TextView speedView;
    @BindView(R.id.relay_time)
    TextView relayTimeView;
    @BindView(R.id.load_size)
    TextView sizeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_download);
        XslUtils.hideStausbar(new WeakReference<>(this), true);
        ButterKnife.bind(this);
    }

    long taskId = 0;
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

            taskId = Aria.download(getContext())
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

    @Override
    protected void onResume() {
        super.onResume();
        if (taskId != 0) {
            Aria.download(this)
                    .load(taskId)     //读取任务id
                    .resume();    // 恢复任务
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (taskId != 0) {
            Aria.download(this)
                    .load(taskId)     //读取任务id
                    .stop();    // 恢复任务
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
        String formatSpeed = formatFileSize(task.getSpeed());
        speedView.setText(formatSpeed);
        long speed = task.getSpeed();
        if (speed != 0) {
            int relayTime = (int) ((task.getFileSize() - task.getCurrentProgress()) / speed);
            String strRelayTime = "剩余时间:";
            relayTimeView.setText(strRelayTime + relayTime);
        }

        sizeView.setText("" + (int) (task.getCurrentProgress()>>20) + "MB");
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

    void entryMainPlay() {
        Intent intent = new Intent();
        intent.putExtra(getResources().getString(R.string.download_result), RequestCode.MainPlay_req);
        setResult(RESULT_OK, intent);
        finish();
    }

    @BindView(R.id.tips)
    TextView textView;
    @OnClick(R.id.download_btn)
    void beginDownLoad(View view) {
        if (downloadFinish) {
            entryMainPlay();
        } else {
            boolean needDownload = false;
            for (int i=0; i!= 12; i++) {
                String strFile = Environment.getExternalStorageDirectory() + "/360/";
                strFile += i;
                strFile += ".mp4";
                if (!FileUtils.isFileExists(strFile)) {
                    RestoreParamMng.getInstance().setNeedCacheState(true);
                    needDownload = true;
                }
                break;
            }
            if (needDownload) {
                startDownload();
                downloadBtn.setVisibility(View.GONE);
                textView.setText("正在缓存");
                bar.setVisibility(View.VISIBLE);
                bar.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        //直接消费掉触摸事件
                        return true;
                    }
                });

                bar.setMax(100);
            } else {
                entryMainPlay();
            }
        }
    }

    /**
     * 格式化文件大小
     *
     * @param size file.length() 获取文件大小
     */
    public static String formatFileSize(double size) {
        if (size < 0) {
            return "0kb";
        }
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "b";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    @OnClick(R.id.back_btn)
    void back() {
        Intent intent = new Intent();
        intent.putExtra(getResources().getString(R.string.download_result), -1);
        setResult(RESULT_OK, intent);
        finish();
    }

    @DownloadGroup.onTaskComplete() void taskComplete(DownloadGroupTask task) {
        Log.d(TAG, "任务组下载完成");
        textView.setText("缓存成功");
        downloadBtn.setVisibility(View.VISIBLE);
        downloadBtn.setText("点击进入");
        bar.setVisibility(View.GONE);
        downloadFinish = true;
        RestoreParamMng.getInstance().setNeedCacheState(false);
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
