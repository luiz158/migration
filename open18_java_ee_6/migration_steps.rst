################################################################################
Seam in Action Open18 to Java EE 6 Migration
################################################################################

This tutorial will walk through converting the Open18 application from Dan 
Allen's *Seam in Action* from Seam 2 to Java EE 6. It will be as plain Java EE 6
as possible. However, there are two large gaps in this migration which will
require projects outside the core of Java EE 6: CRUD scaffolding and application
security. For those two sections CDI Query and Apache Shiro will be used. The
application will also be upgraded to RichFaces 4.1 (the current release at time 
of writing).

********************************************************************************
Migrate to Maven
********************************************************************************

Seam 2, prior to 2.3, used Apache Ant as it's build tool of choice. In migrating 
to vanilla Java EE 6 or when using CDI extensions written by others, Apache 
Maven (or similar such as Gradle, Builder, Ant+Ivy, SBT, etc) will be the
preferred build tool. 

Setup Maven Structure
================================================================================

Apache Maven follows a standard project layout defined below::

  ├── pom.xml
  └── src
      ├── main
      │   ├── java
      │   ├── resources
      │   │   └── META-INF
      │   └── webapp
      │       └── WEB-INF
      └── test
          ├── java
          └── resources

A full explanation of Apache Maven is beyond this tutorial. More information
about Apache Maven (probably more than you'd ever want to know) is available 
from Sonatype at `Maven: The Complete Reference <http://www.sonatype.com/books/mvnref-book/reference/>`_.

The pom.xml file defines all of the dependencies of the project and instructions
for building the project. The directory structure is fairly self explanatory. 
Application source lives under src/main/java. All of the tests are under 
src/test/java. Additional resources for the application such as persistence.xml
will be at src/main/resources/META-INF, and lastly, JSF views and other web
assets such as images, css, javascript, etc. will be under src/main/webapp.

Flesh out pom.xml
================================================================================

Below is the contents for the pom.xml file. There's very little there in terms
of dependencies because the bulk of the dependencies will be provided for the
project by the application server. It is important to note that any dependency
that must be present for either compilation or runtime but is already within the
application server be stated in the pom and given the "provided" scope. Further
information about the sections of the pom.xml file are given as comments in the
actual file::

  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>

      <groupId>org.open18</groupId>
      <artifactId>open18</artifactId>
      <packaging>war</packaging>
      <version>2.0.0.Beta-SNAPSHOT</version>
      <name>Open18 application from Seam in Action</name>

      <properties>
          <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
          <jboss.javaee6.version>2.1.0.Beta1</jboss.javaee6.version>
          <richfaces.version>4.1.0.Final</richfaces.version>
          <apache.shiro.version>1.2.0</apache.shiro.version>
          <arquillian.version>1.0.0.CR7</arquillian.version>
          <junit.version>4.10</junit.version>
      </properties>

      <dependencyManagement>
          <dependencies>
              <dependency>
                  <groupId>org.jboss.spec</groupId>
                  <artifactId>jboss-javaee6-specs-bom</artifactId>
                  <version>${jboss.javaee6.version}</version>
                  <type>pom</type>
                  <scope>import</scope>
              </dependency>
              <dependency>
                  <groupId>org.richfaces</groupId>
                  <artifactId>richfaces-bom</artifactId>
                  <version>${richfaces.version}</version>
                  <type>pom</type>
                  <scope>import</scope>
              </dependency>
              <dependency>
                  <groupId>org.jboss.arquillian</groupId>
                  <artifactId>arquillian-bom</artifactId>
                  <version>1.0.0.CR7</version>
                  <type>pom</type>
                  <scope>import</scope>
              </dependency>
          </dependencies>
      </dependencyManagement>

      <dependencies>
          <dependency>
              <groupId>javax.enterprise</groupId>
              <artifactId>cdi-api</artifactId>
              <scope>provided</scope>
          </dependency>
          <dependency>
              <groupId>org.jboss.spec.javax.faces</groupId>
              <artifactId>jboss-jsf-api_2.1_spec</artifactId>
              <scope>provided</scope>
          </dependency>
          <dependency>
              <groupId>org.jboss.spec.javax.el</groupId>
              <artifactId>jboss-el-api_2.2_spec</artifactId>
              <scope>provided</scope>
          </dependency>
          <dependency>
              <groupId>javax.validation</groupId>
              <artifactId>validation-api</artifactId>
              <scope>provided</scope>
          </dependency>
          <dependency>
              <groupId>org.hibernate.javax.persistence</groupId>
              <artifactId>hibernate-jpa-2.0-api</artifactId>
              <scope>provided</scope>
          </dependency>
          <dependency>
              <groupId>org.jboss.spec.javax.ejb</groupId>
              <artifactId>jboss-ejb-api_3.1_spec</artifactId>
              <scope>provided</scope>
          </dependency>
          <dependency>
              <groupId>org.apache.shiro</groupId>
              <artifactId>shiro-core</artifactId>
              <version>${apache.shiro.version}</version>
              <scope>compile</scope>
          </dependency>
          <dependency>
              <groupId>org.apache.shiro</groupId>
              <artifactId>shiro-web</artifactId>
              <version>${apache.shiro.version}</version>
              <scope>runtime</scope>
          </dependency>
          <dependency>
              <groupId>org.richfaces.ui</groupId>
              <artifactId>richfaces-components-ui</artifactId>
              <scope>runtime</scope>
          </dependency>
          <dependency>
              <groupId>org.richfaces.core</groupId>
              <artifactId>richfaces-core-impl</artifactId>
              <scope>runtime</scope>
          </dependency>
          <dependency>
              <groupId>junit</groupId>
              <artifactId>junit</artifactId>
              <version>${junit.version}</version>
              <scope>test</scope>
          </dependency>
          <dependency>
              <groupId>org.jboss.arquillian.junit</groupId>
              <artifactId>arquillian-junit-container</artifactId>
              <scope>test</scope>
          </dependency>
      </dependencies>

      <build>
          <finalName>${project.artifactId}</finalName>
          <plugins>
              <plugin>
                  <artifactId>maven-compiler-plugin</artifactId>
                  <version>2.3.2</version>
                  <configuration>
                      <source>1.6</source>
                      <target>1.6</target>
                  </configuration>
              </plugin>
          </plugins>
      </build>

  </project>


********************************************************************************
Migrate to JPA 2.0
********************************************************************************

JSR 317, the update to the Java Persistence API includes a number of updates,
many of which users had been asking for including improved mappings, a criteria
API, ordering of collections, eviction control, access to a second level cache,
and locking improvements. Setup and configuration is the same as the initial JPA
specification, as is usage.

Additional information can be found at the `migration guide to AS7 <https://docs.jboss.org/author/display/AS71/How+do+I+migrate+my+application+from+AS5+or+AS6+to+AS7#HowdoImigratemyapplicationfromAS5orAS6toAS7-UpdateyourHibernate3applicationtouseHibernate4>`_.

There have been issues in the past with Seam 2 when using a Seam Managed
Persistence Context and having entities become detached or issues with
transactions. This migration recommends using a transaction scoped Persistence
Context and using EJBs as backing beans. This allows declarative transaction
control and a familiar Persistence Context injection strategy. Due to this
change, use of the ``EntityManager.merge()`` function is required when using
entities which may have become detached from a previous transaction (or
request). Also recommended is the use of the ``@Version`` annotation and column
in the entities to allow for optimistic locking.

Update persistence.xml to 2.0
================================================================================

JPA 2 is backwards compatible with JPA 1. All entities should work correctly as
they did using a JPA 1 implementation. The version in persistence.xml should be
updated to take advantage of new features though. Such features include the type
safe criteria api, new mappings, and additional methods.

Metamodel Generation
================================================================================

To take full advantage of type saftey, static meta model classes should be
created or generated. The simplest way of doing this is using an annotation
processor such as Hibernate's JPA 2 Metamodel Generator. Additional information
on using this annotation processor can be found in `the documentation <http://docs.jboss.org/hibernate/jpamodelgen/1.1/reference/en-
US/html_single/>`_.

.. todo: should I actually go through the steps?

For this migration, the annotation processor was used once and then removed from
the pom.xml file.

Migrate to Bean Validation from Hibernate Validator 3
================================================================================

Java EE 6 contains another specification which standardized validation: JSR 303
- Bean Validation. `Hibernate Validator 4 <http://hibernate.org/subprojects/validator.html`_ 
(4.2.0 is shipped with AS7) is the reference implementation. This is a
completely different code base and includes all new package, validations and
ways of interacting with those validations. If the application is only using
the annotations, these are typically a package change and at times an
annotation change. For example, the ``@org.hibernate.validator.Length``
validation becomes the ``@javax.validation.constraints.Size`` annotation. In
some cases, such as the GolferValidator in Open18, this can become a custom
constraint. Information about custom constraints can be found at the 
`Hibernate Validator documentation <http://docs.jboss.org/hibernate/validator/4.2/reference/en-US/html/validator-customconstraints.html>`_.

For more information about migrating from Hibernate Validator 3, please see `the migration documentation <https://docs.jboss.org/author/display/AS71/How+do+I+migrate+my+application+from+AS5+or+AS6+to+AS7#HowdoImigratemyapplicationfromAS5orAS6toAS7-MigratetoHibernate4Validator>`_.

********************************************************************************
Migrate to CDI
********************************************************************************

 ----- AQUI INICIO -----
With JEE6, JSR 330 came up bringing some specifications about Dependency Injection. By that time, there were several frameworks that could handle with DI into java world, such as Spring, Guice, PicoContainer and Seam 2. With the new JSR, the main idea of all these frameworks finally got registered, but all the implementation became out of standards.

Unfortunately, DI spec had some big missing points. To handle that and complement some topics, a new specification was released - JSR 299 - CDI - Contexts and Dependency Injection. Now scopes could be given to injected resources and so on.

So Seam2 lost one of its main reasons to exists as a framework out of JSRs - Dependency Injection was now documented. So, migrating from Seam 2 to Seam 3 brings you from JEE5 to JEE6 - and this last one includes both DI and CDI specs.

*Injecting resources
Seam 2 had the annotation @In for injecting resources. Now the JSR standards have the annotation @Injection. We shall replace them.

*The scopes
Use @Inject basically makes your container instantiate (use new) a new resource for you. But for how long should this resource live? To answer that, we need a context. Basically, CDI has some specified contexts:

- Application
- Requested
- Session
- Conversation
- Dependent

If you don not specify any scope, by default you have Dependent scope, which means that injected resource assumes the scope of the component it's injected in.

All the scopes can be specified using their respective annotations. Notice that conversation scope is basically Seam 2 Conversation scope, so now you have a standard. 

.. todo: There is no seam.properties but you will need beans.xml
 ----- AQUI MEIO -----
Java EE 6 had a few new additions to the platform, two of them combining to
formally standardize dependency injection for the Enterprise Edition of Java.
These two JSRs are `JSR 330 <http://jcp.org/en/jsr/summary?id=330>`_, which 
defines the annotations used for injection, and `JSR 299
<http://jcp.org/en/jsr/summary?id=299>`_ which defines how dependency resolution
and injection works, scopes for the platform similar to what Seam 2 provided,
and possibly the most important of all: extensibility for the platform. These
two specifications were developed with input from authors of other dependency
injection solutions in Java such as Spring, Guice, and Seam

With these specifications at least two features of Seam 2 had become part of the
platform. Also many of the features Seam 2 had for working JSF also became part
of the JSF specification. Migration from Seam 2 to Java EE 6 makes sense, and
isn't terribly difficult (of course this depends on some of the features that
were used from Seam 2).  

Activation
================================================================================

Seam 2 required the use of the seam.properties file to mark a jar, or WEB-
INF/classes as containing Seam 2 components. This was mainly an optimization
for scanning purposes. CDI has a similar requirement. Each Bean Archive (jar,
war, etc. containing CDI beans) must contain a META-INF/beans.xml for a jar and
WEB-INF/beans.xml for a war. Some configuration may occur in this file, but
often times it can be left blank. In this migration of Open18 the following
beans.xml is used (src/main/webapp/WEB-INF/beans.xml)::

  <?xml version="1.0" encoding="UTF-8"?>
  <beans xmlns="http://java.sun.com/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="
                http://java.sun.com/xml/ns/javaee
                http://docs.jboss.org/cdi/beans_1_0.xsd">
  </beans>
 ----- AQUI FIM -----


Substitute Seam 2 annotations for CDI equivalents
================================================================================

Nearly all of the annotations that were Seam 2 based have equivalents in Java EE
6, however, some of them do not or are no longer needed.

Injecting resources
--------------------------------------------------------------------------------

Because Seam components were typically not managed by the container
(unless an EJB happened to be a Seam component, such as a SFSB or SLSB)
all injection has handled used Seam's ``@In``. As mentioned earlier, Java
EE 6 has standardized Dependency Injection using JSR 330. The annotation
now is ``@javax.inject.Inject``. All of the ``@In`` annotations will need
to be replaced.

There is also a difference in defining what is a bean (or a component in Seam
2). In Seam 2 all components needed to be annotated with the ``@Name``
annotation. This is no longer the case. Each class (there are some exceptions,
please refer to JSR 299 or a CDI implementation documentation) with a no-args
constructor is now a managed bean (not to be confused with the JSF Managed
Bean). There is, however the ``@javax.inject.Named`` annotation. It's main
purpose is to register an EL name for the bean. If the bean is not going to be
used in an EL expression, it is not needed.

Producing resources
--------------------------------------------------------------------------------

Seam 2 had a feature called factories which allowed a resource to be created and
outjected. It allowed for a more custom creation than what Seam could do by
calling the no-args constructor. CDI a similar feature called producers. There
are two big differences between factories and producers and the way both
platforms handle proxies.

* Producers are called once for the scope, similar to scoping a factory, 
  however, they cannot be changed and "re-produced" similar to some approaches
  that have been done with Seam 2.
* Factories do not support injection. With a producer, each parameter is an
  injected resource.

Because of the first difference, it, at times can be necessary to to create a
wrapper around the actual object desired and modify the information as needed.
For example, the list of new golfers in the Open18 application could be produced
and scoped as a ``@SessionScoped`` resource, but it would never change for that
session. If the list were wrapped within another object, the internal list could
be modified if a new golfer registered during the session and the existing
session could then see the new golfer in the list.

In Open18, besides the example mentioned, another resource which must be
produced which Seam 2 had readily available out of the box is the collection of
messages. This is really a simple ResourceBundle, but it isn't available out of
the box. This allows for a combination of messages similar to what Seam 2
offered, though done in Java code instead of components.xml.

Scopes
--------------------------------------------------------------------------------

Scopes are nothing new when coming from Seam 2. The standard scopes still exist
when using CDI:

* ``@ApplicationScoped``
* ``@SessionScoped``
* ``@ConversationScoped``
* ``@RequestScoped``

There is no business process scope or method scope however. CDI has one
other scope which does not exist in Seam 2: ``@DependentScoped``. This scope
is similar in life as a typical Java object creation. It will last as long
as the containing object survives. There's also one important difference,
when injected, the inject object is the actual object not a proxy like the
other scopes. This scope is also the default scope if no scope is specified
for the bean.

If the need arises for additional scopes, such as a business process scope, CDI
allows for additional scopes to be created. Please refer to the JSR 299 spec or
CDI implementation documentation for defining scopes.

Migrate Query  / Home objects
================================================================================

The application framework within Seam 2 consisting of Home and Query objects has
proved to be very powerful for CRUD based sites. When coupled with seam-gen, it
rivals that of other frameworks such as Grails, Ruby on Rails and the like.
There were some glaring holes with it though. Using inheritance instead of
composition, lack of being able to search for null fields, inability to perform
joins, etc. Java EE 6 doesn't have anything ready to use to fill this gap.
Fortunately a little creativity and the JPA Criteria API can go a long way.

In this example a base dao abstract class has been created to keep things DRY. A
similar approach could be done with composition, however, some of the type
safety would be lost. This base class contains all of the functionality for the
DAO, including a dynamic search similar to the Seam 2 Query search idea.

To fill the Home object from Seam 2, simple backing beans which manage an
instance of the entity work nicely, and little code is needed to create a full
replacement when using the DAO to perform all the needed functions. For this
migration each entity has a simple (no code in the child class unless needed for
queries) DAO created, and also a backing bean for each entity to act as the
buffer between the view and the backend. These backing beans also happen to be
Stateful Session beans in this instance. It's not required, but the advantages
of SFSBs have been enumerated many times throughout the years. These backing
beans are annotated with one of the scope annotations mentioned earlier and also
with ``@Named`` so they can be used in EL.

.. sidebar:: WARNING

  It is best not to directly use JPA entities created by CDI, unless
  they are created by a producer. If CDI manages the life cycle of an entity, JPA
  functionality is lost and the entire object will have to be cloned into a new
  object to be persisted.

.. todo: Many have restrictions, will have to see how to recreate this.

.. todo: Trying to use abstract classes to simplify the searching and make it 
  similar to what was done in Seam 2

Changes in the conversation model
================================================================================

CDI has a conversation state similar to Seam 2, however, there are some major
differences. The largest being that only one conversation can be active at a
time per session. This means no nested conversations or multiple conversations
via different browser tabs and also no workspace manager. The conversation,
until CDI 1.1, is also tied directly to JSF and cannot be used outside of JSF
and still remain portable. There is also no annotation control over the
conversation. Instead the conversation must be injected and then managed
(started, ended, timeout configured, etc.).

The conversation can still be tracked by using a query parameter for JSF GET
requests, the name is ``conversationId``. However, using a conversation outside 
of JSF will require additional work, and non portable changes to an application,
unless a new scope is created for the application which behaves like the
conversation from Seam 2.

********************************************************************************
Migrate to  JSF 2.0
********************************************************************************

Seam 2 contained many enhancements to JSF 1.2. Many of these enhancements made
it into the official JSF 2 (JSR 314) specification! Some of these enhancements
include ``h:link`` and ``h:button``, ``f:metadata`` and ``f:viewparam``. Also 
included in JSF 2 is facelets as the preferred view description language. All of
the power of facelets which was use in Seam 2 applications is now available
standard. Composite Components also made their debut in JSR 314 as an easier way
to create JSF components and reusable templates.

Because there are many JSF related enhancements in Seam 2, there are a number of
actions needed to happen to migrate successfully to JSF 2.

.. todo: Also will need something to replace CourseComparison
  ProfileAction needs a replacement possibly from Shiro
  MultiRoundAction needs a Java replacement, or we could just update it CDI
  RegisterAction needs a replacement, may be part of switching to Shiro

Update faces-config.xml to 2.0
================================================================================

Similar to Seam 2, the faces-config.xml file is very sparse, and essentially
becomes a marker file to include JSF support. Below is a typical JSF 2 faces-
config.xml file::

  <?xml version='1.0' encoding='UTF-8'?>
  <faces-config version="2.0" xmlns="http://java.sun.com/xml/ns/javaee"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-facesconfig_2_0.xsd">

  </faces-config>

In the Open18 application, there were multiple languages supported. That part
will need to remain::

    <application>
        <locale-config>
            <default-locale>en</default-locale>
            <supported-locale>bg</supported-locale>
            <supported-locale>de</supported-locale>
            <supported-locale>en</supported-locale>
            <supported-locale>fr</supported-locale>
            <supported-locale>tr</supported-locale>
        </locale-config>
    </application>

The main changes, as listed above in the example are an update for the schema,
the version and the removal of the view handler declaration.

Migrate to RichFaces 4.1
================================================================================

Migrating to JSF 2 also involves an update to the component library. Open18 made
use of RichFaces. True JSF 2 support in RichFaces came out with version
4.0.0.Final. Currently RichFaces 4.1.0.Final is out and 4.2.0.CR1 is also
available. For many components the switch is change of JAR files, however, some
components have not yet been migrated, or others have been combined. Information
about component migration can be found on the `RichFaces wiki <https://community.jboss.org/wiki/RichFacesMigrationGuide33x-4xMigration>`_.

Rework Navigation from pages.xml
================================================================================

Two changes in JSF 2 which Seam influenced are in navigation. Navigation
enhancements include implicit navigation and also conditional navigation,
similar to conditions in pages.xml from Seam 2. These two features have been
covered `in <http://java.dzone.com/articles/fluent-navigation-jsf-2>`_ `many <http://mkblog.exadel.com/2009/09/learning-jsf2-navigation/>`_ 
`places <http://andyschwartz.wordpress.com/2009/07/31/whats-new-in-jsf-2/#navigation>`_. 
While slightly more work in some cases, using a combination of these two
features navigation from pages.xml should be fairly straight forward.

While not directly related to navigation, page actions and params also have
`corresponding solutions <http://andyschwartz.wordpress.com/2009/07/31/whats-new-in-jsf-2/#get>`_ 
in JSF 2. Any number of view parameters can be assigned to a view. They also can
participate in conversion and validation, which is more powerful than what Seam
2 offered in pages.xml. A view action in JSF 2 can be done by creating a
listener for the ``preRenderView`` event within an ``f:metadata`` section.

Seam Tags and equivalents in JSF and RichFaces
================================================================================

Seam 2 introduced some useful JSF components, some which made navigation easier, others which are useful for conversation. The navigation components are simple to migration, while some of the others are a little more difficult and a small collection do not have any replacement.

The first step for migrating these tags is to remove the seam namespace from the view. Below is a table of the tags in Seam 2 and replacements either in JSF 2 or RichFaces.

.. list-table::
   :widths: 15 40
   :header-rows: 1

   * - Seam 2 Tag
     - JSF 2 or RichFaces
   * - ``s:div``
     - No direct mapping. Could be done with an ``h:panelGroup layout="block``
       or a ``ui:fragment`` containing a div.
   * - ``s:fragment``
     - ``ui:fragment``
   * - ``s:link``
     - ``h:link`` action maps to outcome, and there is no propagation attribute.
   * - ``s:button``
     - ``h:button`` same conditions as ``h:link``
   * - ``s:decorate``
     - There is no direct mapping for this, however the same functionality can
       be achieved with the ``UIInputContainer`` and a composite container, both of
       which are in the Open18 migration example.
   * - ``s:label``
     - No direct mapping, but ``h:outputLabel`` is similar.
   * - ``s:span``
     - No direct mapping, but similar output can be achieved by ``h:panelGroup``
       or a ``ui:fragment`` with a ``span`` element
   * - ``s:message``
     - No direct mapping for the same functionality, though ``rich:message``
       could be used instead.
   * - ``s:validateAll``
     - f:validateBean or rich:validator can achieve similar affects.
   * - s:convertDateTime
     - A similar affect can be achieved by using the standard
       ``f:convertDateTime`` and setting the locale, or setting the context-param
       ``javax.faces.DATETIMECONVERTER_DEFAULT_TIMEZONE_IS_SYSTEM_TIMEZONE`` to
       true, as defined in the spec in section 11.1.3. Dan Allen `blogged
       <http://in.relation.to/Bloggers/StepRightUpAndSelectYourTimeZone>`_ about
       this before the spec was final, however, nothing was changed.
   * - ``s:convertEnum``
     - No direct mapping. A custom converter is recommended.
   * - ``s:enumItem``
     - No direct mapping
   * - ``s:selectItems``
     - ``h:selectItems``
   * - ``s:defaultAction``
     - No direct mapping

********************************************************************************
Migrate to Apache Shiro for Security
********************************************************************************

.. todo: AuthenticationManager goes away and uses Shiro, need to figure out how to produce the current golfer
  The auth package goes away and uses Shiro, need to figure out what to do about captcha

********************************************************************************
Further Information
********************************************************************************

More information about migrating from Seam 2:

* `Jozef Hartinger's Diploma Thesis (PDF) <http://is.muni.cz/th/207788/fi_m/jharting-thesis.pdf?lang=en>`_
* `Classic module mentioned in the above thesis <https://github.com/jharting/classic>`_
* `Some comparison of Seam 2 and Java EE 6 <https://github.com/seam/migration/wiki>`_

Additional CDI documentation:

* `Weld, the Reference Implementation of CDI by JBoss <http://seamframework.org/Weld>`_
* `OpenWebBeans, a CDI Implementation done by Apache <http://openwebbeans.apache.org/owb/index.html>`_
* `CanDI, another CDI Implementation done by Caucho <http://www.caucho.com/resin-application-server/candi-java-dependency-injection/>`_

Subsitute technologies:

* `CDI Query, replacement for Home / Query <http://ctpconsulting.github.com/query>`_
* `DataValve, replacement for Home / Query <http://www.andygibson.net/files/datavalve/docs/html/index.html>`_
* `Seam3-persistence-framework, replacement for Home / Query <http://it-crowd.com.pl/blog/seam3_persistence_framework_comes_to_town.html>`_
