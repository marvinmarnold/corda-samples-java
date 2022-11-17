package net.corda.samples.tokentofriend.webserver;

import com.r3.corda.lib.accounts.workflows.flows.CreateAccount;
import net.corda.core.transactions.SignedTransaction;
import net.corda.samples.tokentofriend.flows.CreateMyToken;
import net.corda.samples.tokentofriend.flows.IssueToken;
import net.corda.samples.tokentofriend.flows.QueryToken;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import java.util.*;

import net.corda.samples.tokentofriend.flows.TransferToken;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Stream;

import java.util.stream.Collectors;

import static java.lang.Float.parseFloat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
public class Controller {
    private static final Logger logger = LoggerFactory.getLogger(RestController.class);
    private final CordaRPCOps proxy;
    private final CordaX500Name me;

    public Controller(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
        this.me = proxy.nodeInfo().getLegalIdentities().get(0).getName();

    }
    private boolean isMe(NodeInfo nodeInfo){
        return nodeInfo.getLegalIdentities().get(0).getName().equals(me);
    }

    private boolean isNotary(NodeInfo nodeInfo) {
        return !proxy.notaryIdentities()
                .stream().filter(el -> nodeInfo.isLegalIdentity(el))
                .collect(Collectors.toList()).isEmpty();
    }

    private boolean isNetworkMap(NodeInfo nodeInfo){
        return nodeInfo.getLegalIdentities().get(0).getName().getOrganisation().equals("Network Map Service");
    }

    /** Helpers for filtering the network map cache. */
    public String toDisplayString(X500Name name){
        return BCStyle.INSTANCE.toString(name);
    }

    @GetMapping(value = "/me",produces = APPLICATION_JSON_VALUE)
    private HashMap<String, String> whoami(){
        HashMap<String, String> myMap = new HashMap<>();
        myMap.put("me", me.toString());
        return myMap;
    }

    @GetMapping(value = "/peers", produces = APPLICATION_JSON_VALUE)
    public HashMap<String, List<String>> getPeers() {
        HashMap<String, List<String>> myMap = new HashMap<>();

        // Find all nodes that are not notaries, ourself, or the network map.
        Stream<NodeInfo> filteredNodes = proxy.networkMapSnapshot().stream()
                .filter(el -> !isNotary(el) && !isMe(el) && !isNetworkMap(el));
        // Get their names as strings
        List<String> nodeNames = filteredNodes.map(el -> el.getLegalIdentities().get(0).getName().toString())
                .collect(Collectors.toList());

        myMap.put("peers", nodeNames);
        return myMap;
    }

    @RequestMapping(value = "/mint", method = RequestMethod.POST)
    public ResponseEntity<String> createToken(@RequestBody  String payload){

        System.out.println(payload);
        JsonObject convertedObject = new Gson().fromJson(payload,JsonObject.class);
        float amount = convertedObject.get("amount").getAsFloat();

        try {
            SignedTransaction result = proxy.startTrackedFlowDynamic(IssueToken.class, amount).getReturnValue().get();
            return ResponseEntity.status(HttpStatus.CREATED).body(result.toString());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/retrieve", method = RequestMethod.POST)
    public ResponseEntity<String> retrieveToken(@RequestBody  String payload){

        System.out.println(payload);
        JsonObject convertedObject = new Gson().fromJson(payload,JsonObject.class);
        String receiver = convertedObject.get("username").toString();
        String receiverStr = receiver.substring(1,receiver.length()-1);

        try {
            String result = proxy.startTrackedFlowDynamic(QueryToken.class, receiverStr).getReturnValue().get().toString();
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    public ResponseEntity<String> transfer(@RequestBody  String payload){

        System.out.println(payload);
        JsonObject convertedObject = new Gson().fromJson(payload,JsonObject.class);
        String senderUsername = convertedObject.get("senderUsername").toString();
        String senderUsernameStr = senderUsername.substring(1,senderUsername.length()-1);
        String receiverUsername = convertedObject.get("receiverUsername").toString();
        String receiverUsernameStr = receiverUsername.substring(1,receiverUsername.length()-1);
        String amount = convertedObject.get("amount").toString();
        String amountStr = amount.substring(1,amount.length()-1);

        try {
            String result = proxy.startTrackedFlowDynamic(TransferToken.class, senderUsernameStr, receiverUsernameStr, parseFloat(amountStr)).getReturnValue().get().toString();
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/setup2", method = RequestMethod.POST)
    public ResponseEntity<String> setup(){

        try {
            String result = proxy.startTrackedFlowDynamic(CreateMyToken.class).getReturnValue().get().toString();
            return ResponseEntity.status(HttpStatus.CREATED).body("success22 " + result);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("failed11 " + e.getMessage());
        }
    }
}