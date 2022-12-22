{
  fetchMavenArtifact,
  lib,
  linkFarmFromDrvs,
}: let
  defaultRepos = [
    "https://repo1.maven.org/maven2/"
    "https://repo.clojars.org/"
    "http://oss.sonatype.org/content/repositories/releases/"
    "http://oss.sonatype.org/content/repositories/public/"
    "http://repo.typesafe.com/typesafe/releases/"
  ];
in
  {
    # Name of the derivation output
    name ? "jars-classpath",
    # List of maven repositories from where to fetch artifacts.
    # Example: [ http://oss.sonatype.org/content/repositories/public ].
    repos ? defaultRepos,
    # List of jar artifacts to retrieve from the artifactory.
    jars ? [],
  }: let
    inherit (builtins) map;
    inherit (lib) mergeAttrs;

    # Collect jars from different artifactories
    fetchJarArtifact =
      map (
        dep: (
          # ensure we have at least a place to fetch the dependency
          assert (repos != []) || (dep.url != "") || (dep.urls != []);
          # TODO: We can add support to other providers like Ivy
            fetchMavenArtifact (mergeAttrs dep.maven {inherit repos;})
        )
      )
      jars;

    # Creates an aggregated derivation of jars
    # TODO: We can add different ways of collecting distributing the jars inside the derivation
    classpathBuilder = linkFarmFromDrvs name fetchJarArtifact;
  in
    classpathBuilder
