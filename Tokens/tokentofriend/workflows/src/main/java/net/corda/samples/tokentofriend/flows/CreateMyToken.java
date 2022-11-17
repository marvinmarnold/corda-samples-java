package net.corda.samples.tokentofriend.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.workflows.flows.CreateAccount;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;

@StartableByRPC
public class CreateMyToken extends FlowLogic<Boolean>{

    @Override
    @Suspendable
    public Boolean call() throws FlowException {
        subFlow(new CreateAccount("admin"));
        subFlow(new CreateAccount("merchant1"));
        subFlow(new CreateAccount("merchant2"));
        subFlow(new CreateAccount("merchant3"));
        return true;
    }
}
