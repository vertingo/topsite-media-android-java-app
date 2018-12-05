package com.vertin_go.topsiteapp;

/**
 * Created by VertinGo on 13/09/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;

public class RemotePDFActivity extends BaseSampleActivity implements DownloadFile.Listener, View.OnClickListener
{
    LinearLayout root;
    RemotePDFViewPager remotePDFViewPager;
    EditText etPdfUrl;
    Button btnDownload;
    PDFPagerAdapter adapter;
    public Button fullPageScreenshot;
    public LinearLayout rootContent;
    public ImageView imageView;
    public TextView hiddenText;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle(R.string.remote_pdf_example);
        setContentView(R.layout.activity_remote_pdf);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());

        root = findViewById(R.id.remote_pdf_root);
        etPdfUrl = findViewById(R.id.et_pdfUrl);
        btnDownload = findViewById(R.id.btn_download);

        Intent intent = getIntent();
        // On suppose que tu as mis un String dans l'Intent via le putExtra()
        String url = intent.getStringExtra("url");

        etPdfUrl.setText(url);

        setDownloadButtonListener();

        findViews();
        implementClickEvents();

    }


    /*  Find all views Ids  */
    public void findViews()
    {
        fullPageScreenshot = findViewById(R.id.full_page_screenshot);
        rootContent = findViewById(R.id.remote_pdf_root);
    }

    /*  Implement Click events over Buttons */
    public void implementClickEvents()
    {
        fullPageScreenshot.setOnClickListener(this);
        //customPageScreenshot.setOnClickListener(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (adapter != null)
        {
            adapter.close();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.full_page_screenshot:
                takeScreenshot(ScreenshotType.FULL);
                break;
        }
    }

    /*  Method which will take screenshot on Basis of Screenshot Type ENUM  */
    public void takeScreenshot(ScreenshotType screenshotType)
    {
        Bitmap b = null;
        switch (screenshotType)
        {
            case FULL:
                //If Screenshot type is FULL take full page screenshot i.e our root content.
                b = ScreenshotUtils.getScreenShot(rootContent);
                break;
        }

        //If bitmap is not null
        if (b != null)
        {
            File saveFile = ScreenshotUtils.getMainDirectoryName(this);//get the path to save screenshot
            File file = ScreenshotUtils.store(b, "screenshot" + screenshotType + ".jpg", saveFile);//save the screenshot to selected path
            shareScreenshot(file);//finally share screenshot
        }
        else
            //If bitmap is null show toast message
            Toast.makeText(this, R.string.screenshot_take_failed, Toast.LENGTH_SHORT).show();

    }


    /*  Share Screenshot  */
    public void shareScreenshot(File file)
    {
        Uri uri = Uri.fromFile(file);//Convert file path into Uri for sharing
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.sharing_text));
        intent.putExtra(Intent.EXTRA_STREAM, uri);//pass uri here
        startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
    }

    protected void setDownloadButtonListener()
    {
        final Context ctx = this;
        final DownloadFile.Listener listener = this;
        btnDownload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                remotePDFViewPager = new RemotePDFViewPager(ctx, getUrlFromEditText(), listener);
                remotePDFViewPager.setId(R.id.pdfViewPager);
                hideDownloadButton();
            }
        });
    }

    protected String getUrlFromEditText()
    {
        return etPdfUrl.getText().toString().trim();
    }

    public static void open(Context context)
    {
        Intent i = new Intent(context, RemotePDFActivity.class);
        context.startActivity(i);
    }

    public void showDownloadButton()
    {
        btnDownload.setVisibility(View.VISIBLE);
    }

    public void hideDownloadButton()
    {
        btnDownload.setVisibility(View.INVISIBLE);
    }

    public void updateLayout()
    {
        root.removeAllViewsInLayout();
        root.addView(etPdfUrl,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        root.addView(btnDownload,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        root.addView(fullPageScreenshot,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        root.addView(remotePDFViewPager,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onSuccess(String url, String destinationPath)
    {
        adapter = new PDFPagerAdapter(this, FileUtil.extractFileNameFromURL(url));
        remotePDFViewPager.setAdapter(adapter);
        updateLayout();
        showDownloadButton();
    }

    @Override
    public void onFailure(Exception e)
    {
        e.printStackTrace();
        showDownloadButton();
    }

    @Override
    public void onProgressUpdate(int progress, int total)
    {

    }
}


