package com.example.arcore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.animation.ModelAnimator;
import com.google.ar.sceneform.rendering.AnimationData;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private AnchorNode anchorNode;
    private ModelAnimator animator;
    private int nextAnimation;
    private FloatingActionButton btn_animation;
    private ModelRenderable animationEarth;
    private TransformableNode transformableNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (ArFragment)getSupportFragmentManager().findFragmentById(R.id.sceneform);

        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                if(animationEarth == null)
                    return;

                Anchor anchor = hitResult.createAnchor();
                if(anchorNode == null) {//no hay animacion pintada
                    anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    transformableNode = new TransformableNode(arFragment.getTransformationSystem());
                    //medida modelo
                    transformableNode.getScaleController().setMinScale(0.09f);
                    transformableNode.getScaleController().setMaxScale(0.1f);
                    transformableNode.setParent(anchorNode);
                    transformableNode.setRenderable(animationEarth);

                }
            }
        });

        arFragment.getArSceneView().getScene().addOnUpdateListener(new Scene.OnUpdateListener() {
            @Override
            public void onUpdate(FrameTime frameTime) {
                if(anchorNode == null){
                    if(btn_animation.isEnabled()){
                        btn_animation.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                        btn_animation.setExpanded(false);
                    }
                }else{
                    if(!btn_animation.isEnabled()){
                        btn_animation.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorAccent));
                        btn_animation.setExpanded(false);
                    }
                }
            }
        });

        btn_animation = (FloatingActionButton)findViewById(R.id.btn_anim);
        btn_animation.setEnabled(false);
        btn_animation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(animator == null || !animator.isRunning()){
                    AnimationData data = animationEarth.getAnimationData(nextAnimation);
                    nextAnimation = (nextAnimation + 1)%animationEarth.getAnimationDataCount();
                    animator = new ModelAnimator(data, animationEarth);
                    animator.start();
                }
            }
        });

        setupModel();
    }

    private void setupModel(){
        ModelRenderable.builder().setSource(this,R.raw.novelo_earth).build().
                thenAccept(renderable -> animationEarth = renderable)
                    .exceptionally(throwable -> {
                        Toast.makeText(this, ""+throwable.getMessage(),
                                Toast.LENGTH_SHORT).show();
                                return null;
                    });
    }
}
