package api.mercury;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.junit.jupiter.api.Test;
import org.nervos.ckb.type.transaction.Transaction;
import org.nervos.ckb.utils.AmountUtils;
import org.nervos.mercury.GsonFactory;
import org.nervos.mercury.model.TransferPayloadBuilder;
import org.nervos.mercury.model.common.AssetInfo;
import org.nervos.mercury.model.req.From;
import org.nervos.mercury.model.req.Mode;
import org.nervos.mercury.model.req.Source;
import org.nervos.mercury.model.req.To;
import org.nervos.mercury.model.req.ToInfo;
import org.nervos.mercury.model.req.item.ItemFactory;
import org.nervos.mercury.model.resp.TransactionCompletionResponse;
import org.nervos.mercury.regression.test.RpcMethods;
import org.nervos.mercury.regression.test.domian.cases.CaseWriter;
import org.nervos.mercury.regression.test.domian.rpc.RpcService;

import java.io.IOException;
import java.util.Arrays;

import constant.AddressWithKeyHolder;
import constant.ApiFactory;
import utils.SignUtils;

public class TransferCompletionTest {
  Gson g = GsonFactory.newGson();
  RpcService rpc = new RpcService("http://127.0.0.1:8116");

  CaseWriter cw =
      new CaseWriter(
          RpcMethods.BUILD_TRANSFER_TRANSACTION,
          "/Users/zjh/Documents/cryptape/mercury-devops/mercury-test/regression-test/src/main/resources");

  @Test
  void testSingleFromSingleTo() {
    TransferPayloadBuilder builder = new TransferPayloadBuilder();
    builder.assetInfo(AssetInfo.newCkbAsset());
    builder.from(
        From.newFrom(
            Arrays.asList(
                ItemFactory.newIdentityItemByAddress(AddressWithKeyHolder.testAddress1())),
            Source.Free));
    builder.to(
        To.newTo(
            Arrays.asList(
                new ToInfo(AddressWithKeyHolder.testAddress2(), AmountUtils.ckbToShannon(100))),
            Mode.HoldByFrom));

    try {
      JsonElement blockInfo =
          rpc.post(
              RpcMethods.BUILD_TRANSFER_TRANSACTION,
              g.fromJson(g.toJson(builder.build()), JsonObject.class));

      cw.write("testSingleFromSingleTo", builder.build(), blockInfo);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  void testSingleFromMultiTo() {
    TransferPayloadBuilder builder = new TransferPayloadBuilder();
    builder.from(
        From.newFrom(
            Arrays.asList(
                ItemFactory.newIdentityItemByAddress(AddressWithKeyHolder.testAddress1())),
            Source.Free));

    builder.to(
        To.newTo(
            Arrays.asList(
                new ToInfo(AddressWithKeyHolder.testAddress2(), AmountUtils.ckbToShannon(100)),
                new ToInfo(AddressWithKeyHolder.testAddress3(), AmountUtils.ckbToShannon(100))),
            Mode.HoldByFrom));

    try {
      JsonElement blockInfo =
          rpc.post(
              RpcMethods.BUILD_TRANSFER_TRANSACTION,
              g.fromJson(g.toJson(builder.build()), JsonObject.class));

      cw.write("testSingleFromMultiTo", builder.build(), blockInfo);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  void testMultiFromSingleTo() {
    TransferPayloadBuilder builder = new TransferPayloadBuilder();
    builder.from(
        From.newFrom(
            Arrays.asList(
                ItemFactory.newIdentityItemByAddress(AddressWithKeyHolder.testAddress1()),
                ItemFactory.newIdentityItemByAddress(AddressWithKeyHolder.testAddress2())),
            Source.Free));

    builder.to(
        To.newTo(
            Arrays.asList(
                new ToInfo(AddressWithKeyHolder.testAddress3(), AmountUtils.ckbToShannon(100))),
            Mode.HoldByFrom));

    System.out.println(g.toJson(builder.build()));

    try {
      JsonElement blockInfo =
          rpc.post(
              RpcMethods.BUILD_TRANSFER_TRANSACTION,
              g.fromJson(g.toJson(builder.build()), JsonObject.class));

      cw.write("testMultiFromSingleTo", builder.build(), blockInfo);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  void testPayFee() {
    TransferPayloadBuilder builder = new TransferPayloadBuilder();

    builder.from(
        From.newFrom(
            Arrays.asList(
                ItemFactory.newIdentityItemByAddress(AddressWithKeyHolder.testAddress1())),
            Source.Free));

    builder.to(
        To.newTo(
            Arrays.asList(
                new ToInfo(AddressWithKeyHolder.testAddress2(), AmountUtils.ckbToShannon(100))),
            Mode.HoldByFrom));

    builder.payFee(AddressWithKeyHolder.testAddress3());

    System.out.println(g.toJson(builder.build()));

    try {
      JsonElement blockInfo =
          rpc.post(
              RpcMethods.BUILD_TRANSFER_TRANSACTION,
              g.fromJson(g.toJson(builder.build()), JsonObject.class));

      cw.write("testPayFee", builder.build(), blockInfo);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  void testChange() {
    TransferPayloadBuilder builder = new TransferPayloadBuilder();

    builder.from(
        From.newFrom(
            Arrays.asList(
                ItemFactory.newIdentityItemByAddress(AddressWithKeyHolder.testAddress1())),
            Source.Free));

    builder.to(
        To.newTo(
            Arrays.asList(
                new ToInfo(AddressWithKeyHolder.testAddress2(), AmountUtils.ckbToShannon(100))),
            Mode.HoldByFrom));

    builder.change(AddressWithKeyHolder.testAddress4());

    System.out.println(g.toJson(builder.build()));

    try {
      JsonElement blockInfo =
          rpc.post(
              RpcMethods.BUILD_TRANSFER_TRANSACTION,
              g.fromJson(g.toJson(builder.build()), JsonObject.class));

      cw.write("testChange", builder.build(), blockInfo);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void sendTx(TransferPayloadBuilder builder) throws IOException {
    TransactionCompletionResponse s = ApiFactory.getApi().buildTransferTransaction(builder.build());

    Transaction tx = SignUtils.sign(s);

    System.out.println(g.toJson(tx));
    String txHash = ApiFactory.getApi().sendTransaction(tx);
    System.out.println(txHash);
  }
}
