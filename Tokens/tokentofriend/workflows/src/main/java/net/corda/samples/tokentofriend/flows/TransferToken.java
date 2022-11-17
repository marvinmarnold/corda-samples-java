package net.corda.samples.tokentofriend.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.UtilitiesKt;
import com.r3.corda.lib.accounts.workflows.flows.AccountInfoByKey;
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount;
import com.r3.corda.lib.tokens.contracts.states.FungibleToken;
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType;
import com.r3.corda.lib.tokens.contracts.utilities.AmountUtilities;
import com.r3.corda.lib.tokens.money.MoneyUtilities;
import com.r3.corda.lib.tokens.workflows.flows.move.MoveTokensUtilities;
import com.r3.corda.lib.tokens.workflows.types.PartyAndAmount;
import net.corda.core.contracts.Amount;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Collections.emptyList;

@InitiatingFlow
@StartableByRPC
public class TransferToken extends FlowLogic<SignedTransaction>{

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
    public SignedTransaction call() throws FlowException {
        Party openTransact = getOurIdentity();
        AccountInfo senderAccountInfo = UtilitiesKt.getAccountService(this).accountInfo(senderUsername).get(0).getState().getData();
        AnonymousParty senderAccount = subFlow(new RequestKeyForAccount(senderAccountInfo));

        AccountInfo receiverAccountInfo = UtilitiesKt.getAccountService(this).accountInfo(receiverUsername).get(0).getState().getData();
        AnonymousParty receiverAccount = subFlow(new RequestKeyForAccount(receiverAccountInfo));

        // move money to receiverAccountInfo account.
        final IssuedTokenType issuedUsdTokenType = new IssuedTokenType(openTransact, MoneyUtilities.getUSD());
        final Amount<IssuedTokenType> transferAmount = AmountUtilities.amount(amount, issuedUsdTokenType);
        PartyAndAmount partyAndAmount = new PartyAndAmount(receiverAccount, transferAmount);

        //construct the query criteria and get all available unconsumed fungible tokens which belong to senders account
        QueryCriteria criteria = new QueryCriteria.VaultQueryCriteria().withStatus(Vault.StateStatus.UNCONSUMED)
                .withExternalIds(Arrays.asList(senderAccountInfo.getIdentifier().getId()));

        TransactionBuilder transactionBuilder = new TransactionBuilder();

        //call utility function to move the fungible token from buyer to seller account
        //this also adds inputs and outputs to the transactionBuilder
        //till now we have only 1 transaction with 2 inputs and 2 outputs -
        //one moving fungible tokens other moving non fungible tokens between accounts
        MoveTokensUtilities.addMoveFungibleTokens(transactionBuilder, getServiceHub(),
                Arrays.asList(partyAndAmount), senderAccount, criteria);

        transactionBuilder.verify(getServiceHub());

        FlowSession receiverSession = initiateFlow(receiverAccountInfo.getHost());
        SignedTransaction partiallySignedTx = getServiceHub().signInitialTransaction(transactionBuilder, Arrays.asList(getOurIdentity().getOwningKey()));

        SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(partiallySignedTx, Arrays.asList(receiverSession)));
        return subFlow(new FinalityFlow(fullySignedTx, emptyList()));
    }

    @InitiatedBy(TransferToken.class)
    public static class transferTokenResponderFlow extends FlowLogic<Void> {

        private final FlowSession counterParty;

        public transferTokenResponderFlow(FlowSession counterParty) {
            this.counterParty = counterParty;
        }

        @Suspendable
        @Override
        public Void call() throws FlowException {
            AtomicReference<AccountInfo> receivingAccountRef = new AtomicReference<>();

            SignedTransaction stx = subFlow(new SignTransactionFlow(counterParty) {
                @Override
                protected void checkTransaction(@NotNull SignedTransaction stx) throws FlowException {
                    AbstractParty keyOfReceiver = stx.getCoreTransaction().outRefsOfType(FungibleToken.class).get(0)
                            .getState().getData().getHolder();
                    if (keyOfReceiver != null) {
                        receivingAccountRef.set(subFlow(new AccountInfoByKey(keyOfReceiver.getOwningKey()))
                                .getState().getData());
                    }
                    if (receivingAccountRef.get() == null) {
                        throw new FlowException("Account to receive state was not found on this node");
                    }
                }
            });

            // record and finalize the transaction
            SignedTransaction receivedTx = subFlow(new ReceiveFinalityFlow(counterParty, stx.getId()));
            return null;
        }
    }
}
