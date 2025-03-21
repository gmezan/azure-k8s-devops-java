# azure-k8s-devops-java

1. Create Managed Identity to login to Azure from Actions

    ```sh
    az identity create --name $ID_INFRA_NAME \
        --resource-group $RG_NAME \
        --location $AZ_REGION
    ```


1. Assign _Contributor_ Role to the managed identity for the subscription. Create the following federated credentials

    ```sh
    ID_INFRA_SP=""
    SUBS_ID=""
    az role assignment create --assignee $ID_INFRA_SP \
          --role Contributor \
          --scope $SUBS_ID
          
    az identity federated-credential create --identity-name $ID_INFRA_NAME \
                                      --name onPullRequest \
                                      --resource-group $RG_NAME \
                                      --audiences api://AzureADTokenExchange \
                                      --issuer https://token.actions.githubusercontent.com \
                                      --subject repo:"$OWNER"/"$REPO":pull_request
    
    az identity federated-credential create --identity-name $ID_INFRA_NAME \
                                          --name onBranch \
                                          --resource-group $RG_NAME \
                                          --audiences api://AzureADTokenExchange \
                                          --issuer https://token.actions.githubusercontent.com \
                                          --subject repo:"$OWNER"/"$REPO":ref:refs/heads/main
    
    az identity federated-credential create --identity-name $ID_INFRA_NAME \
                                          --name onEnvironment \
                                          --resource-group $RG_NAME \
                                          --audiences api://AzureADTokenExchange \
                                          --issuer https://token.actions.githubusercontent.com \
                                          --subject repo:"$OWNER"/"$REPO":environment:azure
    ```

1. Configure credentials in GH. Go to the correspondent GH repo. In _Settings_ > _Secrets and variables_ > _Actions_, create the following secrets with the values corresponding to the created managed identity.
   - `AZURE_CLIENT_ID`
   - `AZURE_SUBSCRIPTION_ID`
   - `AZURE_TENANT_ID`

1. Go to the correspondent repo. In _Settings_ > _Environments_, create the `azure` environment.