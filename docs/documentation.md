# Additional Documentation and Links

- [Base POM](base-pom-setup.md)
  - Last updated: 10/05/2025
  - This is meant to be a way to remember what some of the base required items are when setting up a new module.
  - Includes primarily `parent` set up and `build` set up as remainder of items are going to be service specific
  - will update if common packages are found or used and it makes sense to make it more universal
  - All updates prior to learning/investigating service discovery aspects (may cause change)

- [Initial application PDD](cookconnect_pdd.md)
  - Last updated: 09/06/2025
  - This was brainstormed and defined with the help of [Claude](https://claude.ai)
  - Renamed to more applicable "PDD"
    - removed strict performance requirements and other requirement language
    - focus is more on defining overall what is wanted, requirements will be explicitly defined in a different manner

- [Architecture Deviations Analysis](cookconnect_architecture_deviations_v0-0-1.md)
  - Last updated: 10/11/2025
  - Analysis of deviations between the planned architecture in PDD and actual implementation
  - Documents rationale for data model restructuring decisions (list management pattern, ingredient normalization)
  - Explains enhancements to user profiles and social features
  - Maintained fidelity to core microservices principles while improving flexibility

- [Data Domain Documentation](cookconnect_data_domain_v0-0-1.md)
  - Last updated: 10/11/2025
  - Comprehensive reference for all data models, entities, and relationships across all microservices
  - Includes entity definitions for User Service, Recipe Service, and Social Service
  - Documents cross-service integration patterns and data consistency strategy
  - Updated to reflect new package structure (com.tkforgeworks.cookconnect.*)