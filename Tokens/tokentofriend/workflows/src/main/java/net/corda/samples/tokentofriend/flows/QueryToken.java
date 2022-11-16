package net.corda.samples.tokentofriend.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.UtilitiesKt;
import com.r3.corda.lib.tokens.contracts.states.FungibleToken;
import com.r3.corda.lib.tokens.contracts.states.NonFungibleToken;
import com.r3.corda.lib.tokens.contracts.types.TokenType;
import com.r3.corda.lib.tokens.money.MoneyUtilities;
import com.r3.corda.lib.tokens.workflows.utilities.QueryUtilities;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.Party;
import net.corda.samples.tokentofriend.states.CustomTokenState;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;

import java.util.*;

import static com.r3.corda.lib.tokens.workflows.utilities.QueryUtilities.sumTokenCriteria;
import static com.r3.corda.lib.tokens.workflows.utilities.QueryUtilities.tokenAmountWithIssuerCriteria;

@StartableByRPC
public class QueryToken extends FlowLogic<String>{

    private String accountName;

    public QueryToken(String accountName) {
        this.accountName = accountName;
    }

    @Override
    @Suspendable
    public String call() throws FlowException {
        Party openTransact = getOurIdentity();
        //get account info
        AccountInfo account = UtilitiesKt.getAccountService(this).
                accountInfo(accountName).get(0).getState().getData();

        //specify the account id in QueryCriteria
        QueryCriteria belongsToAccountCriteria = new QueryCriteria.VaultQueryCriteria()
                .withExternalIds(Arrays.asList(account.getIdentifier().getId()));

        QueryCriteria queryCriteria = tokenAmountWithIssuerCriteria(MoneyUtilities.getUSD(), openTransact)
                .and(belongsToAccountCriteria)
                .and(sumTokenCriteria());
        Vault.Page<FungibleToken> results = getServiceHub().getVaultService().queryBy(
                FungibleToken.class,
                queryCriteria);
        Amount<TokenType> balance = QueryUtilities.rowsToAmount(MoneyUtilities.getUSD(), results);

        return balance.toString();
    }
}
