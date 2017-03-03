package org.utos.android.safe.dialogs.camTEST.dep;

/**
 * Created by zachariah.davis on 1/24/17.
 */
//public class AttachDepVideoDialogV2 extends DialogFragment implements View.OnClickListener, SurfaceHolder.Callback {
//
//    // https://inducesmile.com/android/android-camera2-api-example-tutorial/
//
//    MediaRecorder recorder;
//    SurfaceHolder holder;
//    boolean recording = false;
//
//    //    Activity activity;
//    Context ctx;
//
//    //    public AttachVideoDialogV2() {
//    //        activity = getActivity();
//    //    }
//
//    //    /* Checks if external storage is available for read and write */
//    //    private static boolean isExternalStorageWritable() {
//    //        String state = Environment.getExternalStorageState();
//    //        return Environment.MEDIA_MOUNTED.equals(state);
//    //    }
//    //
//    //    /* Checks if external storage is available to at least read */
//    //    private static boolean isExternalStorageReadable() {
//    //        String state = Environment.getExternalStorageState();
//    //        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
//    //    }
//
//    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
//        final View inflate = getActivity().getLayoutInflater().inflate(R.layout.dialog_video_dep, null);
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(inflate);
//        //////
//        // Create an instance of Camera
//        recorder = new MediaRecorder();
//        initRecorder();
//
//        FrameLayout cameraView = (FrameLayout) inflate.findViewById(R.id.camera_preview);
////        holder = holder.getHolder();
//        holder.addCallback(this);
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//
//        cameraView.setClickable(true);
//        cameraView.setOnClickListener(this);
//        //////
//
//        // Create the AlertDialog object and return it
//        return builder.create();
//    }
//
//
//    @Override public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//        //        pos = getArguments().getInt("pos");
//        //        list = getArguments().getParcelableArrayList("placesList");
//        //
//        //        mProgressDialog = new ProgressDialog(ctx);
//    }
//
//    @Override public void onAttach(Context context) {
//        super.onAttach(context);
//
//        ctx = context;
//    }
//
//    private void initRecorder() {
//        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
//        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
//
//        CamcorderProfile cpHigh = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
//        recorder.setProfile(cpHigh);
//        recorder.setOutputFile(getVideoFilePath());
//        recorder.setMaxDuration(50000); // 50 seconds
//        recorder.setMaxFileSize(5000000); // Approximately 5 megabytes
//    }
//
//    private void prepareRecorder() {
//        recorder.setPreviewDisplay(holder.getSurface());
//
//        try {
//            recorder.prepare();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//            //            finish();
//        } catch (IOException e) {
//            e.printStackTrace();
//            //            finish();
//        }
//    }
//
//    public void onClick(View v) {
//        if (recording) {
//            recorder.stop();
//            recording = false;
//
//            // Let's initRecorder so we can record again
//            initRecorder();
//            prepareRecorder();
//        } else {
//            recording = true;
//            recorder.start();
//        }
//    }
//
//    public void surfaceCreated(SurfaceHolder holder) {
//        prepareRecorder();
//    }
//
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//    }
//
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        if (recording) {
//            recorder.stop();
//            recording = false;
//        }
//        recorder.release();
//        //        finish();
//    }
//
//    public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
//        Camera.CameraInfo info = new Camera.CameraInfo();
//        Camera.getCameraInfo(cameraId, info);
//        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//        int degrees = 0;
//        switch (rotation) {
//            case Surface.ROTATION_0:
//                degrees = 0;
//                break;
//            case Surface.ROTATION_90:
//                degrees = 90;
//                break;
//            case Surface.ROTATION_180:
//                degrees = 180;
//                break;
//            case Surface.ROTATION_270:
//                degrees = 270;
//                break;
//        }
//
//        int result;
//        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//            result = (info.orientation + degrees) % 360;
//            result = (360 - result) % 360;  // compensate the mirror
//        } else {  // back-facing
//            result = (info.orientation - degrees + 360) % 360;
//        }
//        camera.setDisplayOrientation(result);
//    }
//
//    private String getVideoFilePath() {
//
//        //        File finalDestImage = new File(storageDir.getPath() + File.separator + "video_" + (videoArray.size() + 1) + ".mp4");
//
//        return ((NonUrgentActivity) ctx).REPORT_DIRECTORY_NAME + File.separator + "video_" + (((NonUrgentActivity) getActivity()).videoArray.size() + 1) + ".mp4";
//    }
//
//}