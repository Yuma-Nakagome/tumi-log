package com.example.tumi_log;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// SpringBootTestアノテーションを以下のように変更
// propertiesでデータソースURLを空文字で上書きし、MySQLへの接続を完全に無効化します
@SpringBootTest(properties = {
		"spring.datasource.url="
})
@ActiveProfiles("test") // testプロファイルを有効化する

class TumiLogApplicationTests {

	@Test
	void contextLoads() {
	}

}
