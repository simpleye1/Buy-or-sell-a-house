package com.example.estatepricecalculate;

// MainActivity.java
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etHousePrice, etDeedTaxRate, etIncomeTaxRate, etBuyerAgentFeeRate, etSellerAgentFeeRate, etInitRate;
    private RadioGroup rgDeedTaxBearer, rgIncomeTaxBearer, rgBuyerAgentFeeBearer, rgSellerAgentFeeBearer;
    private RadioButton rbDeedTaxBuyer, rbDeedTaxSeller, rbDeedTaxBoth;
    private RadioButton rbIncomeTaxBuyer, rbIncomeTaxSeller, rbIncomeTaxBoth;
    private RadioButton rbBuyerAgentFeeBuyer, rbBuyerAgentFeeSeller, rbBuyerAgentFeeBoth;
    private RadioButton rbSellerAgentFeeBuyer, rbSellerAgentFeeSeller, rbSellerAgentFeeBoth;
    private Button btnCalculate;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化组件
        etHousePrice = findViewById(R.id.etHousePrice);
        etDeedTaxRate = findViewById(R.id.etDeedTaxRate);
        etIncomeTaxRate = findViewById(R.id.etIncomeTaxRate);
        etBuyerAgentFeeRate = findViewById(R.id.etBuyerAgentFeeRate);
        etSellerAgentFeeRate = findViewById(R.id.etSellerAgentFeeRate);
        etInitRate = findViewById(R.id.etInitRate);

        rgDeedTaxBearer = findViewById(R.id.rgDeedTaxBearer);
        rbDeedTaxBuyer = findViewById(R.id.rbDeedTaxBuyer);
        rbDeedTaxSeller = findViewById(R.id.rbDeedTaxSeller);

        rgIncomeTaxBearer = findViewById(R.id.rgIncomeTaxBearer);
        rbIncomeTaxBuyer = findViewById(R.id.rbIncomeTaxBuyer);
        rbIncomeTaxSeller = findViewById(R.id.rbIncomeTaxSeller);

        rgBuyerAgentFeeBearer = findViewById(R.id.rgBuyerAgentFeeBearer);
        rbBuyerAgentFeeBuyer = findViewById(R.id.rbBuyerAgentFeeBuyer);
        rbBuyerAgentFeeSeller = findViewById(R.id.rbBuyerAgentFeeSeller);

        rgSellerAgentFeeBearer = findViewById(R.id.rgSellerAgentFeeBearer);
        rbSellerAgentFeeBuyer = findViewById(R.id.rbSellerAgentFeeBuyer);
        rbSellerAgentFeeSeller = findViewById(R.id.rbSellerAgentFeeSeller);

        btnCalculate = findViewById(R.id.btnCalculate);
        tvResult = findViewById(R.id.tvResult);

        // 设置按钮点击事件
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("click");
                calculateTransaction();
            }
        });
    }

    private void calculateTransaction() {
        System.out.println("get input");
        // 获取并验证输入
        String housePriceStr = etHousePrice.getText().toString();
        String deedTaxRateStr = etDeedTaxRate.getText().toString();
        String incomeTaxRateStr = etIncomeTaxRate.getText().toString();
        String buyerAgentFeeRateStr = etBuyerAgentFeeRate.getText().toString();
        String sellerAgentFeeRateStr = etSellerAgentFeeRate.getText().toString();
        String initRateStr = etInitRate.getText().toString();

        if (housePriceStr.isEmpty() || deedTaxRateStr.isEmpty() || incomeTaxRateStr.isEmpty()
                || buyerAgentFeeRateStr.isEmpty() || sellerAgentFeeRateStr.isEmpty() || initRateStr.isEmpty()) {
            Toast.makeText(this, "请填写所有输入字段", Toast.LENGTH_SHORT).show();
            return;
        }

        double housePrice, deedTaxRate, incomeTaxRate, buyerAgentFeeRate, sellerAgentFeeRate, initRate;
        try {
            housePrice = Double.parseDouble(housePriceStr) * 10000;
            deedTaxRate = Double.parseDouble(deedTaxRateStr);
            incomeTaxRate = Double.parseDouble(incomeTaxRateStr);
            buyerAgentFeeRate = Double.parseDouble(buyerAgentFeeRateStr);
            sellerAgentFeeRate = Double.parseDouble(sellerAgentFeeRateStr);
            initRate = Double.parseDouble(initRateStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效的数字", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取费用承担方
        FeeBearer deedTaxBearer = getFeeBearer(rgDeedTaxBearer.getCheckedRadioButtonId(),
                rbDeedTaxBuyer);
        FeeBearer incomeTaxBearer = getFeeBearer(rgIncomeTaxBearer.getCheckedRadioButtonId(),
                rbIncomeTaxBuyer);
        FeeBearer buyerAgentFeeBearer = getFeeBearer(rgBuyerAgentFeeBearer.getCheckedRadioButtonId(),
                rbBuyerAgentFeeBuyer);
        FeeBearer sellerAgentFeeBearer = getFeeBearer(rgSellerAgentFeeBearer.getCheckedRadioButtonId(),
                rbSellerAgentFeeBuyer);

        System.out.println("calculate");
        // 计算
        TransactionResult result = calculateTransaction(
                housePrice,
                deedTaxRate,
                incomeTaxRate,
                buyerAgentFeeRate,
                sellerAgentFeeRate,
                deedTaxBearer,
                incomeTaxBearer,
                buyerAgentFeeBearer,
                sellerAgentFeeBearer,
                initRate
        );

        // 显示结果
        tvResult.setText(result.toString());
    }

    private FeeBearer getFeeBearer(int selectedId, RadioButton rbBuyer) {
        if (selectedId == rbBuyer.getId()) {
            return FeeBearer.BUYER;
        } else {
            return FeeBearer.SELLER;
        }
    }

    /**
     * 计算房屋交易中的买方首付和卖方净得金额。
     *
     * @param housePrice            房屋总价，由买方支付。
     * @param deedTaxRate          契税率（例如，0.01 表示 1%）。
     * @param incomeTaxRate        个人所得税率（例如，0.01 表示 1%）。
     * @param buyerAgentFeeRate    买方中介费率（例如，0.01 表示 1%）。
     * @param sellerAgentFeeRate   卖方中介费率（例如，0.005 表示 0.5%）。
     * @param deedTaxBearer        契税承担方。
     * @param incomeTaxBearer      个人所得税承担方。
     * @param buyerAgentFeeBearer  买方中介费承担方。
     * @param sellerAgentFeeBearer 卖方中介费承担方。
     * @return 交易结果，包含买方首付和卖方净得金额。
     */
    private TransactionResult calculateTransaction(
            double housePrice,
            double deedTaxRate,
            double incomeTaxRate,
            double buyerAgentFeeRate,
            double sellerAgentFeeRate,
            FeeBearer deedTaxBearer,
            FeeBearer incomeTaxBearer,
            FeeBearer buyerAgentFeeBearer,
            FeeBearer sellerAgentFeeBearer,
            double initRate
    ) {
        // 计算各项费用
        double deedTax = housePrice * deedTaxRate;
        double incomeTax = housePrice * incomeTaxRate;
        double buyerAgentFee = housePrice * buyerAgentFeeRate;
        double sellerAgentFee = housePrice * sellerAgentFeeRate;

        // 分配契税
        double buyerPayDeedTax = 0;
        double sellerPayDeedTax = 0;
        switch (deedTaxBearer) {
            case BUYER:
                buyerPayDeedTax = deedTax;
                break;
            case SELLER:
                sellerPayDeedTax = deedTax;
                break;
        }

        // 分配个人所得税
        double buyerPayIncomeTax = 0;
        double sellerPayIncomeTax = 0;
        switch (incomeTaxBearer) {
            case BUYER:
                buyerPayIncomeTax = incomeTax;
                break;
            case SELLER:
                sellerPayIncomeTax = incomeTax;
                break;
        }

        // 分配买方中介费
        double buyerPayBuyerAgentFee = 0;
        double sellerPayBuyerAgentFee = 0;
        switch (buyerAgentFeeBearer) {
            case BUYER:
                buyerPayBuyerAgentFee = buyerAgentFee;
                break;
            case SELLER:
                sellerPayBuyerAgentFee = buyerAgentFee;
                break;
        }

        // 分配卖方中介费
        double buyerPaySellerAgentFee = 0;
        double sellerPaySellerAgentFee = 0;
        switch (sellerAgentFeeBearer) {
            case BUYER:
                buyerPaySellerAgentFee = sellerAgentFee;
                break;
            case SELLER:
                sellerPaySellerAgentFee = sellerAgentFee;
                break;
        }

        System.out.println("get result");
        //首付比例
        double buyerInitPayment = housePrice * initRate
                + buyerPayDeedTax
                + buyerPayIncomeTax
                + buyerPayBuyerAgentFee
                + buyerPaySellerAgentFee;;
        // 计算买方总支付金额
        double buyerDownPayment = housePrice
                + buyerPayDeedTax
                + buyerPayIncomeTax
                + buyerPayBuyerAgentFee
                + buyerPaySellerAgentFee;

        // 计算卖方净得金额
        double sellerNetAmount = housePrice
                - sellerPayDeedTax
                - sellerPayIncomeTax
                - sellerPayBuyerAgentFee
                - sellerPaySellerAgentFee;
        System.out.println("buyerInitPayment is "+ buyerInitPayment);

        return new TransactionResult(buyerInitPayment/10000, buyerDownPayment/10000, sellerNetAmount/10000);
    }
}

