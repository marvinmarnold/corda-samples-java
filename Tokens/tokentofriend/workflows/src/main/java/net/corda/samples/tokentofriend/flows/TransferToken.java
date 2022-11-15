package net.corda.samples.tokentofriend.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.UtilitiesKt;
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount;
import com.r3.corda.lib.tokens.contracts.states.FungibleToken;
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType;
import com.r3.corda.lib.tokens.contracts.types.TokenType;
import com.r3.corda.lib.tokens.contracts.utilities.AmountUtilities;
import com.r3.corda.lib.tokens.money.MoneyUtilities;
import com.r3.corda.lib.tokens.workflows.flows.move.MoveTokensUtilities;
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens;
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveFungibleTokens;
import com.r3.corda.lib.tokens.workflows.internal.flows.finality.ObserverAwareFinalityFlow;
import com.r3.corda.lib.tokens.workflows.types.PartyAndAmount;
import com.r3.corda.lib.tokens.workflows.utilities.FungibleTokenBuilder;
import com.r3.corda.lib.tokens.workflows.utilities.QueryUtilities;
import net.corda.core.contracts.Amount;
import net.corda.core.flows.*;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;

import java.util.Arrays;

import static com.r3.corda.lib.tokens.workflows.utilities.QueryUtilities.sumTokenCriteria;
import static com.r3.corda.lib.tokens.workflows.utilities.QueryUtilities.tokenAmountWithIssuerCriteria;

@StartableByRPC
public class TransferToken extends FlowLogic<String>{

    private String senderUsername;
    private String receiverUsername;
    private float amount;

    public TransferToken(String senderUsername, String receiverUsername, float amount) {
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.amount = amount;
    }

    @Override
    @Suspendable
    public String call() throws FlowException {
        return "test";
//        Party openTransact = getOurIdentity();
//        AccountInfo senderAccountInfo = UtilitiesKt.getAccountService(this).accountInfo(senderUsername).get(0).getState().getData();
//        AnonymousParty senderAccount = subFlow(new RequestKeyForAccount(senderAccountInfo));
//
//        AccountInfo receiverAccountInfo = UtilitiesKt.getAccountService(this).accountInfo(receiverUsername).get(0).getState().getData();
//        AnonymousParty receiverAccount = subFlow(new RequestKeyForAccount(receiverAccountInfo));
//
//        // move money to receiverAccountInfo account.
//        final IssuedTokenType issuedUsdTokenType = new IssuedTokenType(openTransact, MoneyUtilities.getUSD());
//        final Amount<IssuedTokenType> transferAmount = AmountUtilities.amount(amount, issuedUsdTokenType);
//        PartyAndAmount partyAndAmount = new PartyAndAmount(receiverAccount, transferAmount);
//
//        //construct the query criteria and get all available unconsumed fungible tokens which belong to senders account
//        QueryCriteria.VaultQueryCriteria criteria = new QueryCriteria.VaultQueryCriteria().withStatus(Vault.StateStatus.UNCONSUMED)
//                .withExternalIds(Arrays.asList(senderAccountInfo.getIdentifier().getId()));
//
//        //call utility function to move the fungible token from buyer to seller account
//        //this also adds inputs and outputs to the transactionBuilder
//        //till now we have only 1 transaction with 2 inputs and 2 outputs -
//        //one moving fungible tokens other moving non fungible tokens between accounts
//        MoveTokensUtilities.addMoveFungibleTokens(transactionBuilder, getServiceHub(),
//                Arrays.asList(partyAndAmount), senderAccount, criteria);
//
//        //establish sessions with buyer and seller. to establish session get the host name from accountinfo object
//        FlowSession customerSession = initiateFlow(senderAccountInfo.getHost());
//        FlowSession dealerSession = initiateFlow(receiverAccountInfo.getHost());
//
//        //Note: though buyer and seller are on the same node still we will have to call CollectSignaturesFlow as the signer is not a Party but an account.
//        SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(selfSignedTransaction,
//                Arrays.asList(customerSession, dealerSession)));
//
//        //call ObserverAwareFinalityFlow for finality
//        subFlow(new ObserverAwareFinalityFlow(fullySignedTx, Arrays.asList(customerSession, dealerSession)));
    }
}
