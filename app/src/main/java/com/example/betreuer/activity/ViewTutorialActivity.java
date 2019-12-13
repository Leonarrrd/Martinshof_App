package com.example.betreuer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.betreuer.R;
import com.example.betreuer.adapter.SliderAdapter;


public class ViewTutorialActivity extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;

    private TextView[] mDots;

    private SliderAdapter sliderAdapter;

    private Button mNextBtn;
    private Button mBackBtn;
    private int mCurrentPage;

    private RadioButton mDoneBtn;
    private boolean readyForNextStep = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tutorial);
        // set title text
        ((TextView) findViewById(R.id.title)).setText(getIntent().getStringExtra("title"));

        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);

        mNextBtn = (Button) findViewById(R.id.nexBtn);
        mBackBtn = (Button) findViewById(R.id.preBtn);
        mDoneBtn = (RadioButton) findViewById(R.id.doneBtn);

        sliderAdapter = new SliderAdapter(this, getIntent().getStringExtra("tutorial"));

        mSlideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(readyForNextStep) {
                    mSlideViewPager.setCurrentItem(mCurrentPage + 1);
                } else {
                    Toast.makeText(ViewTutorialActivity.this, "Du musst erst auf 'erledigt' klicken, um weitermachen zu können.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlideViewPager.setCurrentItem(mCurrentPage - 1);
            }
        });

        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readyForNextStep = true;
                buildButtons(mCurrentPage);
            }
        });
    }

    public void addDotsIndicator(int position){
        mDots = new TextView[sliderAdapter.getPages()];
        mDotLayout.removeAllViews();
        for(int i = 0; i < mDots.length; i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorAccent));

            mDotLayout.addView(mDots[i]);
        }

        if(mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    public void buildButtons(int position){
        if(readyForNextStep) {
            if (position == 0) {
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(false);
                mBackBtn.setVisibility(View.INVISIBLE);

                mNextBtn.setText("Next");
                mBackBtn.setText("");
                mNextBtn.setTextColor(Color.parseColor("#FF000000"));
            } else if (position == mDots.length - 1) {
                mNextBtn.setEnabled(false);
                mBackBtn.setEnabled(true);
                mNextBtn.setVisibility(View.INVISIBLE);
                mBackBtn.setVisibility(View.VISIBLE);

                mBackBtn.setText("Back");
                mBackBtn.setTextColor(Color.parseColor("#FF000000"));
            } else {
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(true);
                mBackBtn.setVisibility(View.VISIBLE);
                mNextBtn.setVisibility(View.VISIBLE);

                mNextBtn.setText("Next");
                mBackBtn.setText("Back");
                mNextBtn.setTextColor(Color.parseColor("#FF000000"));
                mBackBtn.setTextColor(Color.parseColor("#FF000000"));
            }
        } else {
            if (position == 0) {
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(false);
                mBackBtn.setVisibility(View.INVISIBLE);
                mNextBtn.setTextColor(Color.parseColor("#FF88A6C4"));

                mNextBtn.setText("Next");
                mBackBtn.setText("");
            } else if (position == mDots.length - 1) {
                mNextBtn.setEnabled(false);
                mBackBtn.setEnabled(true);
                mNextBtn.setVisibility(View.INVISIBLE);
                mBackBtn.setVisibility(View.VISIBLE);

                mBackBtn.setText("Back");
                mBackBtn.setTextColor(Color.parseColor("#FF88A6C4"));
            } else {
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(true);
                mBackBtn.setVisibility(View.VISIBLE);
                mNextBtn.setVisibility(View.VISIBLE);

                mNextBtn.setText("Next");
                mBackBtn.setText("Back");
                mNextBtn.setTextColor(Color.parseColor("#FF88A6C4"));
                mBackBtn.setTextColor(Color.parseColor("#FF88A6C4"));
            }
        }
    }


    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mDoneBtn.setChecked(false);
            readyForNextStep = false;
            buildButtons(position);

            addDotsIndicator(position);
            mCurrentPage = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
