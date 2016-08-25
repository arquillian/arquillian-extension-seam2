Seam 2 support for Arquillian
============================

* Test enrichment for `@In` injection points from the Seam 2 Context.
* Packaging support for adding the Seam 2 framework.
* Tested on JBoss AS 4.2.3.GA and 5.1.0.Final (both remote and managed).

For more details please refer to [Arquillian Confluence](https://docs.jboss.org/author/display/ARQ/Seam+2).

### Code example

    @Name("fluidOuncesConverter")
    public class FluidOuncesConverter
    {

       public Double convertToMillilitres(Double ounces)
       {
          return ounces * 29.5735296;
       }

    }

    @RunWith(Arquillian.class)
    public class ComponentInjectionTestCase
    {
       @Deployment
       public static Archive<?> createDeployment()
       {
          return ShrinkWrap.create(WebArchive.class, "test.war")
                           .addClass(FluidOuncesConverter.class)
                           .addPackages(true, "org.fest")
                           .addPackages(true, "org.dom4j") // Required for JBoss AS 4.2.3.GA
                           .addAsResource(EmptyAsset.INSTANCE, "seam.properties")
                           .setWebXML("web.xml");
       }

       @In
       FluidOuncesConverter fluidOuncesConverter;

       @Test
       public void shouldInjectSeamComponent() throws Exception
       {
          assertThat(fluidOuncesConverter).isNotNull();
       }

       @Test
       public void shouldConvertFluidOuncesToMillilitres() throws Exception
       {
          // given
          Double ouncesToConver = Double.valueOf(8.0d);
          Double expectedMillilitres = Double.valueOf(236.5882368d);

          // when
          Double millilitres = fluidOuncesConverter.convertToMillilitres(ouncesToConver);

          // then
          assertThat(millilitres).isEqualTo(expectedMillilitres);

       }
    }

Note: if you will face problems fetching some dependencies add following Maven [repositories](https://gist.github.com/1809214).
