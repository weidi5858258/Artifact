package com.weidi.artifact.activity;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;

import com.weidi.artifact.R;
import com.weidi.artifact.ui.PullToRefreshBase;
import com.weidi.artifact.ui.PullToRefreshBase.OnRefreshListener;
import com.weidi.artifact.ui.PullToRefreshListView;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class MusicPlayActivity extends ListActivity implements OnClickListener{
	private Context mContext;
	private PullToRefreshListView mPullRefreshListView;//自定义的ListView
	private ListView actualListView;
	private ImageView backToTop;
	private ArrayAdapter<String> mAdapter;
	private LinkedList<String> mListItems;
	private int firstVisibleItem;
	private int totalItemCount;
	
	private String[] mStrings = { "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
			  "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
			  "Allgauer Emmentaler", "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
			  "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
			  "Allgauer Emmentaler" 
			};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_musicplay);
		mContext = MusicPlayActivity.this;
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {//往下或者往上拉后刷新的操作
				new Thread(new Runnable() {//一般就是联网操作的耗时任务了
					@Override
					public void run() {
						SystemClock.sleep(2000);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if(mPullRefreshListView.currentMode == PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH){
									for(int i=1;i<=20;i++){
										mListItems.addFirst("前面数据"+i);
									}
									mAdapter.notifyDataSetChanged();
									//这里要判断一下有没有刷新出内容来，如果没有的话，减1后会有异常的。
									if(mAdapter.getCount() > MusicPlayActivity.this.totalItemCount && mAdapter.getCount() >= 20){//我想即使每个条目的高度不一致，刷新出来的内容的数量也不一定，但是应该会有方法去取得刷新出来的内容的数量的，能得到这个数量，那么就好办了
										//这里要显示的位置应该是加载出来的内容的数量减1
										actualListView.setSelection(19);//所有条目高度一样，数量一定的情况下可以这么做。否则可能得不到想要的结果。
									}
								}else if(mPullRefreshListView.currentMode == PullToRefreshBase.MODE_PULL_UP_TO_REFRESH){
									//加到最后的那条记录能成功添加，但是不会显示出来，需要手向上滑一下才会显示出来，这种体验不好。
									for(int i=1;i<=20;i++){
										mListItems.addLast("后面数据"+i);
									}
									mAdapter.notifyDataSetChanged();
									//这里也要判断一下有没有刷新出内容来，如果没有的话，加1后会有异常的。
									//下面这句代码一定要在上面这句代码后面执行才有用
									if(mAdapter.getCount() > MusicPlayActivity.this.totalItemCount){
										actualListView.setSelection(MusicPlayActivity.this.firstVisibleItem + 1);
									}
//									actualListView.setSelection(mAdapter.getCount());//向上拉刷新后立即显示出添加的那条信息，上面问题就解决了。
								}
								// Call onRefreshComplete when the list has been refreshed.
								mPullRefreshListView.onRefreshComplete();//不能忘记写上这句，不然状态不会恢复的
							}
						});
					}
				}).start();
			}
		});
		backToTop = (ImageView) findViewById(R.id.lv_backtotop);
		mPullRefreshListView.setBackToTopView(backToTop);
		actualListView = mPullRefreshListView.getRefreshableView();
		mListItems = new LinkedList<String>();
		mListItems.addAll(Arrays.asList(mStrings));//把字符串转化成List集合

		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListItems);
		actualListView.setAdapter(mAdapter);
		//用setSelection()这个方法设置要显示的条目，如果要显示的条目后面还有足够多的条目，那么显示的位置在屏幕最上方；如果后面没有足够多的条目，那么要显示的条目可能位于屏幕最下方，或者中间的位置。
//		actualListView.setSelection(5);//进去后直接显示最后一条信息
		actualListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				MusicPlayActivity.this.firstVisibleItem = firstVisibleItem;
				MusicPlayActivity.this.totalItemCount = totalItemCount;
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mStrings = null;
		mAdapter = null;
		mListItems.clear();
		mListItems = null;
		
	}
	
	@Override
	public void onClick(View v) {
	}
	
	private void deleteFilesByDirectory(File file){
		if(file != null && file.isDirectory() && file.length() == 0){
			System.out.println(file.getAbsolutePath());
			file.delete();
		}else if(file != null && !file.isDirectory() && file.length() == 0){
			System.out.println(file.getAbsolutePath());
			file.delete();
		}
	}
}
