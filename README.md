# CI/CD of Spring App with GitHub Actions to Azure Kubernetes Service Cluster

## Deploy required infrastructure

1. Go to the following GitHub repository to access the Terraform configuration for setting up your **Azure Kubernetes Service (AKS)** infrastructure:
   - [Terraform Infra for AKS on GitHub](https://github.com/gmezan/terraform-infra-gh-actions/tree/azure/aks)

1. Complete the **Terraform and Azure configuration steps**:
   - Fork and Clone the repository to your local machine or directly work in GitHub.
   - Review and modify the Terraform configuration files according to your Azure environment (e.g., update resource group names, region, AKS settings, etc.).
   - Make sure you have the necessary **Azure credentials** to apply the Terraform configuration.

1. Deploy the infrastructure by pushing changes to the **main** branch:
   - Once your configuration is ready, commit and push the changes to the **main** branch in your GitHub repository.
   - The push will trigger the **Azure Terraform Plan/Apply** to automatically apply the Terraform configuration and deploy the infrastructure to Azure.

## Configure Spring App repo connection to Azure

1. Az Login and configure the following variables
   ```sh
   ID_INFRA_NAME=<Managed-Identity-Key-Name>
   RG_NAME=<Resource-Group-Name>
   AZ_REGION=<Azure-Region>
   OWNER=<GH-UserName>
   REPO=<This-Repo-Name>
    ```

1. Create Managed Identity to login to Azure from Actions

    ```sh
    az identity create --name $ID_INFRA_NAME \
        --resource-group $RG_NAME \
        --location $AZ_REGION
    ```


1. Assign _Contributor_ Role to the managed identity for the subscription. Create the following federated credentials

    ```sh
    ID_INFRA_SP=<ID-Service-Principal>
    SUBS_ID="/subscriptions/<Subscription-Id>"
    
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

1. Go to the [App Deployment GH Actions](https://github.com/gmezan/azure-k8s-devops-java/blob/main/.github/workflows/deploy.yml) definition and configure the following according to your infra deployment:
   - `CONTAINER_REGISTRY_NAME`
   - `RELEASE_NAME`
   - `AKS_RESOURCE_GROUP`
   - `AKS_NAME`

## Deploy to Azure

1. Create and merge a PR to `main`. This will trigger the `App Deployment` [GH Action](https://github.com/gmezan/azure-k8s-devops-java/actions/runs/13987043853)
1. `Continuous Integration` [step](https://github.com/gmezan/azure-k8s-devops-java/actions/runs/13987043853/job/39162546740) will publish the image to the Azure Container Registry
   ![image](https://github.com/user-attachments/assets/005527b5-da3c-4105-9d5b-569e65c78df7)

1. `Continuous Delivery` [step](https://github.com/gmezan/azure-k8s-devops-java/actions/runs/13987043853/job/39162604807) will create the deployment to the Azure Kubernetes Service Cluster
   ![image](https://github.com/user-attachments/assets/c4dcc8b5-6e77-4f95-b23c-0a02fb537a08)
