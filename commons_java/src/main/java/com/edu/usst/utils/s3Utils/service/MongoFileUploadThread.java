package com.edu.usst.utils.s3Utils.service;

import com.edu.usst.utils.s3Utils.S3Utils;
import com.edu.usst.utils.s3Utils.UploadConfig;
import com.edu.usst.utils.s3Utils.UploadTask;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Wan on 2016/10/5 0005.
 */
public class MongoFileUploadThread extends FileUploadThreadTemplate {
    static final private AtomicInteger s3UploadThreadIdx = new AtomicInteger(0);

    public MongoFileUploadThread(UploadConfig config, S3Utils s3ClientUtil) {
        super(config, s3ClientUtil, MongoFileUploadThread.class.getName() + "-" + s3UploadThreadIdx.getAndIncrement());
    }

    @Override
    protected UploadTask fetchTask() {
        return uploadConfig.getUploadTaskDao().fetchS3Task();
    }

    @Override
    protected void updateTask(UploadTask uploadTask) {
        uploadConfig.getUploadTaskDao().update(uploadTask);
    }

}
