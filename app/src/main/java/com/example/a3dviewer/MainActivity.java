package com.example.a3dviewer;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Light;
import com.google.ar.sceneform.rendering.ModelRenderable;

public class MainActivity extends AppCompatActivity {

    private SceneView sceneView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sceneView = findViewById(R.id.sceneView);

        // Load the GLB model
        ModelRenderable.builder()
                .setSource(this, Uri.parse("football.glb"))
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(renderable -> {
                    // 🌞 Directional light
                    Light sunLight = Light.builder(Light.Type.DIRECTIONAL)
                            .setColor(new com.google.ar.sceneform.rendering.Color(1.0f, 1.0f, 1.0f))
                            .setIntensity(5000f) // brighter
                            .setShadowCastingEnabled(true)
                            .build();

                    Node lightNode = new Node();
                    lightNode.setLight(sunLight);
                    lightNode.setParent(sceneView.getScene());

                    // 🌎 Ambient light (to avoid flat look)
                    Light ambientLight = Light.builder(Light.Type.DIRECTIONAL)
                            .setColor(new com.google.ar.sceneform.rendering.Color(1f, 1f, 1f))
                            .setIntensity(1000f)
                            .build();

                    Node ambientNode = new Node();
                    ambientNode.setLight(ambientLight);
                    ambientNode.setParent(sceneView.getScene());

                    // 🏐 Add model node
                    AnchorNode anchorNode = new AnchorNode();
                    Node modelNode = new Node();
                    modelNode.setRenderable(renderable);


                    modelNode.setRenderable(renderable);
                    modelNode.setLocalScale(new Vector3(0.2f, 0.2f, 0.2f));
                    modelNode.setLocalPosition(new Vector3(0f, 0f, -29f)); // far away

// Start with slight tilt
                    modelNode.setLocalRotation(
                            Quaternion.axisAngle(new Vector3(0f, 1f, 0f), 30f)
                    );

                    sceneView.getScene().addChild(modelNode);

// 🔄 Auto-rotate animation
                    sceneView.getScene().addOnUpdateListener(frameTime -> {
                        Quaternion currentRotation = modelNode.getLocalRotation();
                        Quaternion deltaRotation = Quaternion.axisAngle(new Vector3(0f, 1f, 0f), 1f); // 1° per frame
                        modelNode.setLocalRotation(Quaternion.multiply(currentRotation, deltaRotation));
                    });


                    anchorNode.addChild(modelNode);
                    sceneView.getScene().addChild(anchorNode);
                })
                .exceptionally(
                        throwable -> {
                            throwable.printStackTrace();
                            return null;
                        }
                );
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            sceneView.resume();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sceneView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sceneView.destroy();
    }
}
