const axios = require('axios');

async function testMerchantAPI() {
    const testData = {
        companyName: "测试汽车租赁有限公司",
        unifiedSocialCreditCode: "91110000MA1234567X",
        legalPersonName: "张三",
        legalPersonIdCard: "110101199001010001",
        contactName: "李四",
        contactPhone: "13800138001",
        companyAddress: "北京市朝阳区测试地址",
        businessLicenseUrl: "",
        transportLicenseUrl: ""
    };

    try {
        const response = await axios.post('http://localhost:3001/api/merchants/apply', testData, {
            headers: {
                'Content-Type': 'application/json',
                'X-Tenant-Id': '1'
            }
        });

        console.log('请求成功:', response.data);
    } catch (error) {
        console.error('请求失败:', error.response?.data || error.message);
    }
}

testMerchantAPI();