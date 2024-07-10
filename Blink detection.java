// Copyright 2019 The MediaPipe Authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.mediapipe.apps.facemeshgpu;

import android.os.Bundle;
import android.util.Log;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmarkList;
import com.google.mediapipe.framework.AndroidPacketCreator;
import com.google.mediapipe.framework.Packet;
import com.google.mediapipe.framework.PacketGetter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import com.google.mediapipe.apps.facemeshgpu.SecondActivity;

 
import android.widget.TextView;

/** Main activity of MediaPipe face mesh app. */
public class MainActivity extends com.google.mediapipe.apps.basic.MainActivity {
    private static final String TAG = "MainActivity";

    private static final String INPUT_NUM_FACES_SIDE_PACKET_NAME = "num_faces";
    private static final String OUTPUT_LANDMARKS_STREAM_NAME = "multi_face_landmarks";
    private static final int NUM_FACES = 1;
    private static final int frame_threshold = 10;

    private float ratio1,ratio2, right_eye_posx, right_eye_posy, right_maxima_x, right_maxima_y, right_minima_x, right_minima_y;
    private float c1,c2, left_eye_posx, left_eye_posy, left_maxima_x, left_maxima_y, left_minima_x, left_minima_y, focus_x, focus_y;
    public TextView textView;
    private boolean blinking,eyes_open,eyes_closed;
    private String text = "";
    private int framecounter;
    private int freq = 0;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
      

        AndroidPacketCreator packetCreator = processor.getPacketCreator();
        Map<String, Packet> inputSidePackets = new HashMap<>();
        inputSidePackets.put(INPUT_NUM_FACES_SIDE_PACKET_NAME, packetCreator.createInt32(NUM_FACES));
        processor.setInputSidePackets(inputSidePackets);      

        processor.addPacketCallback(
                OUTPUT_LANDMARKS_STREAM_NAME,
                (packet) -> {
                    Log.v(TAG, "Received multi face landmarks packet.");
                    List<NormalizedLandmarkList> multiFaceLandmarks =
                            PacketGetter.getProtoVector(packet, NormalizedLandmarkList.parser());

                    

                    c1 = multiFaceLandmarks.get(0).getLandmarkList().get(5).getY()*1920f;
                    c2 = multiFaceLandmarks.get(0).getLandmarkList().get(4).getY()*1920f;

                    right_maxima_x = multiFaceLandmarks.get(0).getLandmarkList().get(386).getX()*1080f;
                    right_minima_x = multiFaceLandmarks.get(0).getLandmarkList().get(373).getX()*1080f;

                    right_maxima_y =  multiFaceLandmarks.get(0).getLandmarkList().get(386).getY()*1920f;
                    right_minima_y = multiFaceLandmarks.get(0).getLandmarkList().get(373).getY()*1920f;

                    left_maxima_x = multiFaceLandmarks.get(0).getLandmarkList().get(159).getX()*1080f;
                    left_minima_x = multiFaceLandmarks.get(0).getLandmarkList().get(145).getX()*1080f;

                    left_maxima_y = multiFaceLandmarks.get(0).getLandmarkList().get(159).getY()*1920f;
                    left_minima_y = multiFaceLandmarks.get(0).getLandmarkList().get(145).getY()*1920f;


                    // change the ay1 and ay2 accordingly

                    left_eye_posx = (left_maxima_x + left_minima_x)/2;
                    left_eye_posy = (left_maxima_y + left_minima_y)/2;

                    right_eye_posx = (right_maxima_x + right_minima_x)/2;
                    right_eye_posy = (right_maxima_y + right_minima_y)/2;

                    focus_x = (left_eye_posx + right_eye_posx) / 2;
                    focus_y = (left_eye_posy + right_eye_posy) / 2;


                    ratio1 = (right_minima_y - right_maxima_y) / (c2 - c1);
                    ratio2 = (left_minima_y - left_maxima_y) / (c2 - c1);
                    


                    //leye_x.setText(left_eye_posx+ ""); 
                   

                    //framecounter++;

                    if(ratio1 < 0.8 || ratio2 < 0.8){

                      //BLINK IS DETECTED!!!!
                    
                    }       


        //setContentView(rootView);     
        
        /*Log.v(
            TAG,
            "[TS:"
                + packet.getTimestamp()
                + "] "
                + getMultiFaceLandmarksDebugString(multiFaceLandmarks));*/
                });
    }

  private static String getMultiFaceLandmarksDebugString( List<NormalizedLandmarkList> multiFaceLandmarks) {

    if (multiFaceLandmarks.isEmpty()) {
      return "No face landmarks";
    }

    String multiFaceLandmarksStr = "Number of faces detected: " + multiFaceLandmarks.size() + "\n";
    int faceIndex = 0;
    for (NormalizedLandmarkList landmarks : multiFaceLandmarks) {
      multiFaceLandmarksStr +=
          "\t#Face landmarks for face[" + faceIndex + "]: " + landmarks.getLandmarkCount() + "\n";
      int landmarkIndex = 0;
      for (NormalizedLandmark landmark : landmarks.getLandmarkList()) {
        multiFaceLandmarksStr +=
            "\t\tLandmark ["
                + landmarkIndex
                + "]: ("
                + landmark.getX()
                + ", "
                + landmark.getY()
                + ", "
                + landmark.getZ()
                + ")\n";
        ++landmarkIndex;
      }
      ++faceIndex;
    }
    return multiFaceLandmarksStr;
  }

}