#!/usr/bin/env bash

set -xe

main () {
  local gitLogin="${1?Missing git login}"
  local gitPassword="${2?Missing git password}"

  {
    printf "machine github.com\n"
    printf "login %s\n" "${gitLogin}"
    printf "password %s\n" "${gitPassword}"

    printf "machine api.github.com\n"
    printf "login %s\n" "${gitLogin}"
    printf "password %s\n" "${gitPassword}"
  } > ~/.netrc

  chmod 600 ~/.netrc
}

main "$@"