# osb-example
## Description

An empty Open Service Broker missing concrete implementation of a distinct service.    
Uses MongoDB Database for management. 
Uses MongoDB Database or CredHub for storing credentials.  
Configuration files and deployment scripts must be added.  
Concrete Service logic and binding logic has to be added. In this state, the broker simulates business logic

## Start with this example
1. Clone it.
2. Build it. `mvn clean install`
3. Provide a valid configuration. 
4. Run it or push it to Cloud Foundry.

##### Example Configuration


    spring:
      ### Profile ###
      profiles: defaut
    
    ### Persistence ###
    #### MongoDB ####
      data:
        mongodb:
          host: $host
          port: 27017
          database: $authDatabase
          username: $user
          password: $password
    
    ### Deployment ###
    #### Existing Service Server/Cluster ####
    existing:
      endpoint:
        hosts: 
          - 127.0.0.1
        port: 11111
        database: foo
        username: bar
        password: rol
    
    ### Service Key Generation ###
    #### HAProxy ####
    haproxy:
      uri: $haporxyUrl
      auth:
        token: $haProxyAuthToken
    
    ### Login Information ### 
    login:
      username: $authUser
      password: $authPassword
      role: USER
    
    catalog:
      services:
        - id: sample-local
          name: Sample-local
          description: Sample Instances
          bindable: true
          dashboard: 
            url: $endpoint_uri
            auth_endpoint: $uaa_uri
          dashboard_client:
            id: sample
            secret: sample
            redirect_uri: $endpoint_uri/dashboard/manage
          plans:
            - id: sample_s_local
              name: S
              description: A simple sample Local plan.
              free: false
              volumeSize: 25
              volumeUnit: M
              platform: EXISTING_SERVICE
              connections: 4



## Start Custom Implementation
To implement custom service creation behaviour implement a Service that inherits from `ExistingServiceFactory` or any already supported CPI PlatformService or 
start from scratch with a new CPI by Implementing the `PlatformService`.   
To manipulate the service binding behaviour you can inherit from `BindingService` or `BindingServiceImpl`.
For the full framework implementation see the documentation in [evoila/osb-docs](https://github.com/evoila/osb-docs)


  
