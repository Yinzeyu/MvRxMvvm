package com.yzy.baselibrary.base

import android.os.Bundle
import com.airbnb.mvrx.BaseMvRxActivity
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxView
import com.airbnb.mvrx.MvRxViewId
import com.yzy.baselibrary.toast.YToast
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.ktx.immersionBar
import org.kodein.di.*
import org.kodein.di.android.*
import org.kodein.di.android.retainedSubKodein
import org.kodein.di.generic.kcontext

abstract class BaseActivity : BaseMvRxActivity(), MvRxView, KodeinAware {
    //MvRxView
    private val mvrxViewIdProperty = MvRxViewId()
    final override val mvrxViewId: String by mvrxViewIdProperty
    override val kodeinTrigger = KodeinTrigger()
    override val kodeinContext: KodeinContext<*> = kcontext(this)
    override val kodein by retainedSubKodein(kodein(), copy = Copy.All) {
        initKodein(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        this.onCreateBefore()
        this.initStatus()
        initBeforeCreateView(savedInstanceState)
        super.onCreate(savedInstanceState)
        setContentView(layoutResId())
        kodeinTrigger.trigger()
        initView()
        initDate()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mvrxViewIdProperty.saveTo(outState)
    }

    /**
     * 页面内容布局resId
     */
    protected abstract fun layoutResId(): Int


    abstract fun initView()
    abstract fun initDate()
    /**
     * 需要在onCreateView中调用的方法
     */
    protected open fun initBeforeCreateView(savedInstanceState: Bundle?) {

    }

    /** 适配状态栏  */
    protected open fun initStatus() {
        immersionBar {
            transparentStatusBar()
            statusBarDarkFont(true)
            statusImmersionBar(this)
        }
    }

    protected open fun statusImmersionBar(immersionBar: ImmersionBar) {

    }
    override fun invalidate() {
    }


    /** 这里可以做一些setContentView之前的操作,如全屏、常亮、设置Navigation颜色、状态栏颜色等  */
    protected open fun onCreateBefore() {}

    protected open fun initKodein(builder: Kodein.MainBuilder) {
    }
    protected fun subscribeVM(vararg viewModels: BaseMvRxViewModel<*>) {
        viewModels.forEach {
            it.subscribe(owner = this, subscriber = {
                postInvalidate()
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //移除所有Activity类型的Toast防止内存泄漏
        YToast.cancelActivityToast(this)
    }

}
