package net.corda.samples.tokentofriend.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.UtilitiesKt;
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount;
import com.r3.corda.lib.tokens.contracts.states.FungibleToken;
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType;
import com.r3.corda.lib.tokens.contracts.utilities.AmountUtilities;
import com.r3.corda.lib.tokens.money.MoneyUtilities;
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens;
import net.corda.core.contracts.Amount;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;

import java.util.Arrays;

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
