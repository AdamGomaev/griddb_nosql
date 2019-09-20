import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.toshiba.mwcloud.gs.Collection;
import com.toshiba.mwcloud.gs.ColumnInfo;
import com.toshiba.mwcloud.gs.Container;
import com.toshiba.mwcloud.gs.ContainerInfo;
import com.toshiba.mwcloud.gs.GSType;
import com.toshiba.mwcloud.gs.GridStore;
import com.toshiba.mwcloud.gs.GridStoreFactory;
import com.toshiba.mwcloud.gs.Row;

public class RemoveRowByRowkey {

	public static void main(String[] args){
		try {
			//===============================================
			// クラスタに接続する
			//===============================================
			// 接続情報を指定する (マルチキャスト方式)
			Properties prop = new Properties();
			prop.setProperty("notificationAddress", "239.0.0.1");
			prop.setProperty("notificationPort", "31999");
			prop.setProperty("clusterName", "myCluster");
			prop.setProperty("database", "public");
			prop.setProperty("user", "admin");
			prop.setProperty("password", "admin");
			prop.setProperty("applicationName", "SampleJava");

			// GridStoreオブジェクトを生成する
			GridStore store = GridStoreFactory.getInstance().getGridStore(prop);
			// コンテナ作成や取得などの操作を行うと、クラスタに接続される
			store.getContainer("dummyContainer");


			//===============================================
			// コンテナ作成&ロウを取得する
			//===============================================
			String containerName = "SampleJava_RemoveRowByRowkey";
			createContainerPutRow(store, containerName);


			//===============================================
			// ロウを取得する
			//===============================================
			// (1)Containerオブジェクトの取得
			Container<Integer, Row> container = store.getContainer(containerName);
			if ( container == null ){
				throw new Exception("Container not found.");
			}

			// (2) ロウキーを指定してロウを削除する
			container.remove(3);

			System.out.println("Remove Row");

			//===============================================
			// 終了処理
			//===============================================
			container.close();
			store.close();
			System.out.println("success!");

		} catch ( Exception e ){
			e.printStackTrace();
		}
	}

	private static void createContainerPutRow(GridStore store, String containerName) throws Exception {
		// コンテナを作成する
		ContainerInfo containerInfo = new ContainerInfo();
		List<ColumnInfo> columnList = new ArrayList<ColumnInfo>();
		columnList.add(new ColumnInfo("id", GSType.INTEGER));
		columnList.add(new ColumnInfo("productName", GSType.STRING));
		columnList.add(new ColumnInfo("count", GSType.INTEGER));
		containerInfo.setColumnInfoList(columnList);
		containerInfo.setRowKeyAssigned(true);

		Collection<Void, Row> collection = store.putCollection(containerName, containerInfo, false);
		System.out.println("Create Collection name=" + containerName);


		// ロウを登録する
		String[] nameList = {"notebook PC", "desktop PC", "keybord", "mouse", "printer"};
		int[] numberList = {108, 72, 25, 45, 62};

		List<Row> rowList = new ArrayList<Row>();
		for ( int i = 0; i < nameList.length; i++ ){
			Row row = collection.createRow();
			row.setInteger(0, i);
			row.setString(1, nameList[i]);
			row.setInteger(2, numberList[i]);
			rowList.add(row);
		}
		collection.put(rowList);
	}
}