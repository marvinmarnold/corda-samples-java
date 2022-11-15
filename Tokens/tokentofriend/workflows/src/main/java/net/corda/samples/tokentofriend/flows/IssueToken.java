package net.corda.samples.tokentofriend.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.tokens.contracts.states.NonFungibleToken;
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType;
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens;
import com.r3.corda.lib.tokens.workflows.utilities.NonFungibleTokenBuilder;
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

import java.util.*;
import java.util.stream.Collectors;

@StartableByRPC
public class IssueToken extends FlowLogic<String>{

    private float amount;

    public IssueToken(float amount) {
        this.amount = amount;
    }

    @Override
    @Suspendable
    public String call() throws FlowException {
        /* Get a reference of own identity */
        Party openTransact = getOurIdentity();

        FungibleToken token = new FungibleTokenBuilder()
                .ofTokenType(MoneyUtilities.getUSD())
                .withAmount(amount)
                .issuedBy(openTransact)
                .heldBy(openTransact)
                .buildFungibleToken();

        subFlow(new IssueTokens(Arrays.asList(token)));

        return "\nMinted "+ amount + "USDs\nStorage Node is: "+storageNode;
    }

    public AbstractParty storageSelector(){
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        List<NodeInfo> allOtherNodes = getServiceHub().getNetworkMapCache().getAllNodes().stream().filter( it ->
                (!it.getLegalIdentities().get(0).equals(getOurIdentity())) && (!it.getLegalIdentities().get(0).equals(notary))
        ).collect(Collectors.toList());
        int pick = (int) (Math.random()*(allOtherNodes.size()*10)/10);
        return allOtherNodes.get(pick).getLegalIdentities().get(0);
    }

}
