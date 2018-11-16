package com.example.pdftronexample

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.pdftron.pdf.config.ViewerConfig
import com.pdftron.pdf.controls.PdfViewCtrlTabFragment
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment
import com.pdftron.pdf.utils.AppUtils
import com.pdftron.pdf.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author Milosz Skalski
 */
class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG_PDF_FRAGMENT = "PDF_FRAGMENT"
        const val SAVE_PERMISSION_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        saveBtn.setOnClickListener { saveBtnClick() }
        AppUtils.initializePDFNetApplication(this.applicationContext)
        if (savedInstanceState == null) {
            openPdfFragment()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SAVE_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            saveBtnClick()
        }
    }

    private fun openPdfFragment() {
        val config = ViewerConfig.Builder()
            .toolbarTitle("Title")
            .multiTabEnabled(false)
            .showSearchView(false)
            .showShareOption(false)
            .showCloseTabOption(false)
            .showBottomNavBar(false)
            .showPrintOption(false)
            .showEditPagesOption(false)
            .showSaveCopyOption(false)
            .showAnnotationToolbarOption(false)
            .showDocumentSettingsOption(false)
            .fullscreenModeEnabled(false)
            .longPressQuickMenuEnabled(false)
            .build()

        val originalFile = Utils.copyResourceToLocal(this, R.raw.sample, "initial_file", ".pdf")
        val args = PdfViewCtrlTabFragment.createBasicPdfViewCtrlTabBundle(this, Uri.fromFile(originalFile), "", config)
        args.putParcelable(PdfViewCtrlTabHostFragment.BUNDLE_TAB_HOST_CONFIG, config)
        val pdfViewCtrlTabHostFragment = PdfViewCtrlTabHostFragment.newInstance(args)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, pdfViewCtrlTabHostFragment, TAG_PDF_FRAGMENT)
            .commit()
    }

    private fun saveBtnClick() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), SAVE_PERMISSION_REQUEST)
            return
        }

        val pdfViewCtrlTabHostFragment = supportFragmentManager.findFragmentByTag(TAG_PDF_FRAGMENT)
        if (pdfViewCtrlTabHostFragment == null || pdfViewCtrlTabHostFragment !is PdfViewCtrlTabHostFragment) {
            return
        }

        val pdfViewCtrl = pdfViewCtrlTabHostFragment.currentPdfViewCtrlFragment.pdfViewCtrl
        val isSuccess = pdfViewCtrl.save("file_edit.pdf", false)
        if (isSuccess) {
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }
}
