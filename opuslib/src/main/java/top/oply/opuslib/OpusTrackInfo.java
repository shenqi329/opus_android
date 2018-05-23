package top.oply.opuslib;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

;

/**
 * Created by young on 2015/8/7.
 */
public class OpusTrackInfo {

    private static volatile OpusTrackInfo oTrackInfo ;
    public static OpusTrackInfo getInstance(){
        if(oTrackInfo == null)
            synchronized(OpusTrackInfo.class){
                if(oTrackInfo == null)
                    oTrackInfo = new OpusTrackInfo();
            }
        return oTrackInfo;
    }

    private String TAG = OpusTrackInfo.class.getName();
    private OpusEvent mEventSender;
    private OpusTool mTool = new OpusTool();
    private String appExtDir;
    private File requestDirFile;
    private Thread mThread = new Thread();
    private AudioPlayList mTrackInforList = new AudioPlayList();
    private Utils.AudioTime mAudioTime = new Utils.AudioTime();

    public static final String TITLE_TITLE = "TITLE";
    public static final String TITLE_ABS_PATH = "ABS_PATH";
    public static final String TITLE_DURATION = "DURATION";
    public static final String TITLE_IMG = "TITLE_IMG";
    public static final String TITLE_IS_CHECKED = "TITLE_IS_CHECKED";
    private  Context context;

    public void setEvenSender(OpusEvent opusEven) {
        mEventSender = opusEven;
    }
    private OpusTrackInfo() {

        //create OPlayer directory if it does not exist.
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return;
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        appExtDir = sdcardPath + "/xtcdata/";
        File fp = new File(appExtDir);
        if(!fp.exists())
            fp.mkdir();

        getTrackInfor(appExtDir);
    }

    public void addOpusFile(String file) {
        try {
            Thread.sleep(10);
        } catch (Exception e) {
            Utils.printE(TAG, e);
        }

        File f = new File(file);
        if(f.exists() && "opus".equalsIgnoreCase(Utils.getExtention(file))
                && mTool.openOpusFile(file) != 0) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(TITLE_TITLE, f.getName());
            map.put(TITLE_ABS_PATH, file);
            mAudioTime.setTimeInSecond(mTool.getTotalDuration());
            map.put(TITLE_DURATION, mAudioTime.getTime());
            map.put(TITLE_IS_CHECKED,false);
            //TODO: get imagin from opus files
            map.put(TITLE_IMG, 0);
            mTrackInforList.add(map);
            mTool.closeOpusFile();

            if(mEventSender != null)
                mEventSender.sendTrackinforEvent(mTrackInforList);
        }
    }

    public String getAppExtDir() {
        return  appExtDir;
    }

    public void sendTrackInforToUi() {
        if(mEventSender != null)
            mEventSender.sendTrackinforEvent(mTrackInforList);
    }
    public AudioPlayList getTrackInfor() {
        return mTrackInforList;
    }

    private void getTrackInfor(String Dir) {
        if(Dir.length() == 0)
            Dir = appExtDir;
        File file = new File(Dir);
        if (file.exists() && file.isDirectory())
            requestDirFile = file;

        mThread = new Thread(new MyThread(), "Opus Trc Trd");
        mThread.start();
    }

    public String getAValidFileName(String prefix){
        return getAValidFileName(prefix,".opus");
    }

    public String getAValidFileName(String prefix,String extention) {
        String name = prefix;
        HashSet<String> set = new HashSet<String>(100);
        List<Map<String, Object>> lst =  getTrackInfor().getList();
        for (Map<String, Object>map : lst) {
            set.add(map.get(OpusTrackInfo.TITLE_TITLE).toString());
        }
        int i = 0;
        while (true) {
            i++;
            if(!set.contains(name + i + extention))
                break;
        }

        return appExtDir + name + i + extention;
    }

    public static long getAmrDuration(File file){
        long duration = -1;
        int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0 };
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            long length = file.length();//文件的长度
            int pos = 6;//设置初始位置
            int frameCount = 0;//初始帧数
            int packedPos = -1;
            /////////////////////////////////////////////////////
            byte[] datas = new byte[1];//初始数据值
            while (pos <= length) {
                randomAccessFile.seek(pos);
                if (randomAccessFile.read(datas, 0, 1) != 1) {
                    duration = length > 0 ? ((length - 6) / 650) : 0;
                    break;
                }
                packedPos = (datas[0] >> 3) & 0x0F;
                pos += packedSize[packedPos] + 1;
                frameCount++;
            }
            /////////////////////////////////////////////////////
            duration += frameCount * 20;//帧数*20
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return duration;
    }

    private void prepareTrackInfor(File file) {
        try {
            File[] files = file.listFiles();
            for(File f : files) {
                if (f.isFile()) {
                    String name = f.getName();
                    String absPath = f.getAbsolutePath();
                    if ("opus".equalsIgnoreCase(Utils.getExtention(name))
                            && mTool.openOpusFile(absPath) != 0) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put(TITLE_TITLE, f.getName());
                        map.put(TITLE_ABS_PATH,absPath);
                        long duration = mTool.getTotalDuration();
                        mAudioTime.setTimeInSecond(duration);
                        map.put(TITLE_DURATION, mAudioTime.getTime());
                        //TODO: get imagin from opus files
                        map.put(TITLE_IS_CHECKED,false);
                        map.put(TITLE_IMG, 0);
                        mTrackInforList.add(map);
                        mTool.closeOpusFile();
                    }else if ("amr".equalsIgnoreCase(Utils.getExtention(name))){
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put(TITLE_TITLE, f.getName());
                        map.put(TITLE_ABS_PATH,absPath);
                        long duration = getAmrDuration(new File(absPath));
                        mAudioTime.setTimeInSecond(duration/1000);
                        map.put(TITLE_DURATION, mAudioTime.getTime());
                        //TODO: get imagin from amr files
                        map.put(TITLE_IS_CHECKED,false);
                        map.put(TITLE_IMG, 0);
                        mTrackInforList.add(map);
                    }else if ("wav".equalsIgnoreCase(Utils.getExtention(name))){
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put(TITLE_TITLE, f.getName());
                        map.put(TITLE_ABS_PATH,absPath);
                        long duration = 13;//getAmrDuration(new File(absPath));
                        mAudioTime.setTimeInSecond(duration/1000);
                        map.put(TITLE_DURATION, mAudioTime.getTime());
                        //TODO: get imagin from amr files
                        map.put(TITLE_IS_CHECKED,false);
                        map.put(TITLE_IMG, 0);
                        mTrackInforList.add(map);
                    }
                } else if (f.isDirectory()){
                    prepareTrackInfor(f);
                }
            }
        } catch (Exception e) {
            Utils.printE(TAG, e);
        }
    }

    public static class AudioPlayList implements Serializable {
        public AudioPlayList() {

        }
        public static final long serialVersionUID=1234567890987654321L;
        private List<Map<String, Object>> mAudioInforList = new ArrayList<Map<String, Object>>(32);

        public void add(Map<String, Object> map) {
            mAudioInforList.add(map);
        }
        public List<Map<String, Object>> getList() {
            return mAudioInforList;
        }
        public boolean isEmpty() {
            return mAudioInforList.isEmpty();
        }
        public int size() {
            return mAudioInforList.size();
        }
        public void clear() {
            mAudioInforList.clear();
        }
    }

    class MyThread implements Runnable {
        public void run() {
            prepareTrackInfor(requestDirFile);
            sendTrackInforToUi();
        }
    }

    public void release() {
        try{
            if(mThread.isAlive())
                mThread.interrupt();
        }catch (Exception e) {
            Utils.printE(TAG, e);
        }
    }
}
