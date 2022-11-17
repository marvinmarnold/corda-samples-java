package net.corda.samples.tokentofriend.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.workflows.UtilitiesKt;
import com.r3.corda.lib.accounts.workflows.flows.AccountInfoByName;
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount;
import com.r3.corda.lib.tokens.contracts.states.
        FungibleToken;
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType;
import com.r3.corda.lib.tokens.contracts.utilities.AmountUtilities;
import com.r3.corda.lib.tokens.contracts.utilities.TransactionUtilitiesKt;
import com.r3.corda.lib.tokens.money.MoneyUtilities;
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens;
import com.r3.corda.lib.tokens.workflows.utilities.FungibleTokenBuilder;
import com.r3.corda.lib.tokens.workflows.utilities.NonFungibleTokenBuilder;
import net.corda.core.contracts.Amount;
import net.corda.core.flows.FinalityFlow;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.samples.tokentofriend.states.CustomTokenState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;

import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;

@StartableByRPC
public class IssueToken extends FlowLogic<SignedTransaction>{

    private static String ACEPAY_ACCOUNT_NAME = "admin";
    private float amount;

    public IssueToken(float amount) {
        this.amount = amount;
    }

    @Override
    @Suspendable
    public SignedTransaction call() throws FlowException {
        /* Get a reference of own identity */
        Party openTransact = getOurIdentity();
        AccountInfo acepayAccountInfo = UtilitiesKt.getAccountService(this).accountInfo(ACEPAY_ACCOUNT_NAME).get(0).getState().getData();
        AnonymousParty acepayAccount = subFlow(new RequestKeyForAccount(acepayAccountInfo));

        final IssuedTokenType issuedUsdTokenType = new IssuedTokenType(openTransact, MoneyUtilities.getUSD());
        final Amount<IssuedTokenType> issuedUsdAmount = AmountUtilities.amount(amount, issuedUsdTokenType);
        FungibleToken fungibleToken = new FungibleToken(issuedUsdAmount, acepayAccount, null);

        return subFlow(new IssueTokens(Arrays.asList(fungibleToken)));
    }
}
