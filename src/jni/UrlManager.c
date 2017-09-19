#include "UrlManager.h"
#include <string.h>

typedef struct {
	char *key;
	char *value;
} URL_MAP;

static const URL_MAP urlMap[] = {

		// 生产环境地址
		{"PRODUCT_URL", "https://node.xianglin.cn/ggw/mgw.htm"},


		// 预生产环境地址
		{"PP_PRODUCT_URL", "https://ppnode.xianglin.cn/ggw/mgw.htm"},

		{"ENV_PP_LONGLINK_HOST","xlapp-pp.xianglin.cn"},
		{"ENV_PP_LONGLINK_PORT","9001"},

		{"ENV_PP_FILE_HOST","xlapp-pp.xianglin.cn"},
		{"ENV_PP_FILE_PORT","9002"},


		// 开发环境地址
		{"ENV_DEV_URL", "http://ggw-dev.xianglin.cn/ggw/mgw.htm"},
		/**
		 * //xlapp-dev.xianglin.cn:9001	xlappserver.dev.xianglin.com	xlappserver	9999 //开发长连接
		 * //xlapp-dev.xianglin.cn:9002	xlappfile.dev.xianglin.com	xlappfile	8888//开发文件
		 */
		{"ENV_DEV_LONGLINK_HOST","xlapp-dev.xianglin.cn"},
		{"ENV_DEV_LONGLINK_PORT","9001"},
		{"ENV_DEV_FILE_HOST","xlapp-dev.xianglin.cn"},
		{"ENV_DEV_FILE_PORT","9002"},

		// 测试环境地址
		{"ENV_TEST_URL", "http://ggw-test.xianglin.cn/ggw/mgw.htm"},
		/**
		 * 172.16.6.33	admin	#af@B6cUtt	xlapp-test.xianglin.cn:9004	xlappfile.test.xianglin.com      	xlappfile	8888 //测试文件
		 * 172.16.6.34	admin	#af@B6cUtt	xlapp-test.xianglin.cn:9002	xlappserver.test.xianglin.com    	xlappserver	9999 //测试长连接
		 */
		{"ENV_TEST_LONGLINK_HOST","xlapp-test.xianglin.cn"},
		{"ENV_TEST_LONGLINK_PORT","9003"},
		{"ENV_TEST_FILE_HOST","xlapp-test.xianglin.cn"},
		{"ENV_TEST_FILE_PORT","9004"},


		// 测试环境2地址
		{"ENV_TEST_2_URL", "http://ggw-test2.xianglin.cn/ggw/mgw.htm"},

		{"ENV_TEST_2_LONGLINK_HOST","xlapp-test2.xianglin.cn"},
		{"ENV_TEST_2_LONGLINK_PORT","9003"},

		{"ENV_TEST_2_FILE_HOST","xlapp-test2.xianglin.cn"},
		{"ENV_TEST_2_FILE_PORT","9004"},

		// 联调环境地址
		{"ENV_LT_URL", "http://ggw.lt.xianglin.com/ggw/mgw.htm"},





};

char *getUrlByKey(char *inKey) {
	int i = 0;
	// 数组长度
	int len = sizeof(urlMap) / sizeof(URL_MAP);
	while (i < len) {
		if (strcmp(urlMap[i].key, inKey) == 0) {
			return urlMap[i].value;
		}
		i++;
	}
	return NULL;
}
