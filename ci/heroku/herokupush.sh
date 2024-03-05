#!/bin/bash

# TODO. DELETE COMMENTED ECHOS
# IDEA: DO A FUNCTION TO EASILY UPDATE ENV VARIABLES

########## Constants
########## You are allow to edit this
ABOUT="A script to simplify current workflow to deploy heroku repos"

LANDING_PATCH="landing"
ACQUISITION_PATCH="acquisition"
ENTITY_PATCH="entity"
COMPANY_PATCH="company"
BO_PATCH="backoffice"
WORKER_PATCH="worker" # worker / schedule

LANDING_MODULE="la"
ACQUISITION_MODULE="ac"
ENTITY_MODULE="entity"
COMPANY_MODULE="company"
BO_MODULE="bo"
WORKER_MODULE="worker" # worker / schedule

DEV_ENV="dev"
STG_ENV="stg"
PRD_ENV="prd"

########## Default variables
PATCH_DIRECTORY="./ci/heroku"

########## Required variables
PATCH=
REMOTE_ENV_TO_DEPLOY=
HEROKU_MODULE=
CURRENT_BRANCH=

########## Functions

usage() {
    echo -e "
    $ABOUT\n
    Usage: ./herokupush.sh <environment> [commands...]
    \t<environment>
    \t-dev | --remote-dev\tDeploys to Development environment
    \t-stg | --remote-stage\tDeploys to Staging environment
    \t-prd | --remote-prd\tDeploys to Production environment
    \t[commands]
    \t--patches\t\tDirectory where the patches are. Defaults to: $PATCH_DIRECTORY
    \t-a  | --all\t\tApply patch and deploy every single module * WIP *
    \t-la | --only-landing\tDeploys just Landing module
    \t-ac | --only-acq\tDeploys just Acquisition module
    \t-ee | --only-entity\tDeploys just Entity Extranet module
    \t-ce | --only-company\tDeploys just Company Extranet module
    \t-bo | --only-backoffice\tDeploys just Backoffice module
    \t-wo | --only-worker\tDeploys just Worker module
    "
}

# Find .git directory
# Validate if is repository
is_git_repo() {
    if [[ ! $(find . -iname '.git')  ]]
    then
        echo "Directory is not a git repository, please move the script or execute \"git init\" here."
        exit 1
    fi
}

# Execute git log
# Take 1st line
# Cut the string delimiting by ' ' and taking the 2nd part
get_last_commit_hash() {
    LAST_COMMIT_HASH=$(git log | head -n1 | cut -d' ' -f 2)
#    echo "LAST_COMMIT_HASH $LAST_COMMIT_HASH"
}

get_current_branch_name() {
    CURRENT_BRANCH=$(git branch --list | grep \* | cut -d' ' -f 2)
#    echo "CURRENT_BRANCH $CURRENT_BRANCH"
}

# Check if there are untracked or unstaged files
check_if_untracked_unstaged_files() {
    if [[ $(git status | grep -i commit | head -n 1) != *"nothing to commit"* ]]
        then
            echo "You still have untracked/unstaged files. Add/Commit then first."
            exit 1
        fi
}

# Apply the patch
# Add and commit
# Push local branch to heroku
# Reset to previous commit
# Revert changes
do_the_job() {
#    echo "REMOTE_ENV_TO_DEPLOY $REMOTE_ENV_TO_DEPLOY"
#    echo "PATCH $PATCH"
#    echo "HEROKU_MODULE $HEROKU_MODULE"

    if [[ "$REMOTE_ENV_TO_DEPLOY" == "" ]]
        then
            echo "You must choose the environment to deploy."
            exit 1
    elif [[ "$PATCH" == "" || "$HEROKU_MODULE" == "" ]]
        then
            echo "You did not specify the module to update."
            exit 1
    fi

    eval "git apply $PATCH_DIRECTORY/heroku_$PATCH.patch"
    git add .
    git commit --allow-empty-message -m ''
    eval "git push heroku-solven-$HEROKU_MODULE-$REMOTE_ENV_TO_DEPLOY $CURRENT_BRANCH:master -f"
    git reset "$LAST_COMMIT_HASH"
    git checkout .
}

########## End of functions

########## Main
# There's no time to explain. Read this: http://linuxcommand.org/lc3_wss0120.php
# You have to check if PATCH and HEROKU_MODULE are correct
while (( "$#" )); do
#    echo $1
    case $1 in
    -dev | --remote-dev)    REMOTE_ENV_TO_DEPLOY=$DEV_ENV
                            ;;
    -stg | --remote-stg)    REMOTE_ENV_TO_DEPLOY=$STG_ENV
                            ;;
    -prd | --remote-prd)    REMOTE_ENV_TO_DEPLOY=$PRD_ENV
                            ;;
    --patches)              PATCH_DIRECTORY=$1
                            ;;
#        TODO ALL
#    -a | --all)             PATCH="ALL"
#                            HEROKU_MODULE="ALL"
#                            ;;
    -la | --only-landing)   PATCH=$LANDING_PATCH
                            HEROKU_MODULE=$LANDING_MODULE
                            ;;
    -ac | --only-acq)       PATCH=$ACQUISITION_PATCH
                            HEROKU_MODULE=$ACQUISITION_MODULE
                            ;;
    -ee | --only-entity)    PATCH=$ENTITY_PATCH
                            HEROKU_MODULE=$ENTITY_MODULE
                            ;;
    -ce | --only-company)   PATCH=$COMPANY_PATCH
                            HEROKU_MODULE=$COMPANY_MODULE
                            ;;
    -bo | --only-backoffice)PATCH=$BO_PATCH
                            HEROKU_MODULE=$BO_MODULE
                            ;;
    -wo | --only-worker)    PATCH=$WORKER_PATCH
                            HEROKU_MODULE=$WORKER_MODULE
                            ;;
    -h | --help )           usage
                            exit
                            ;;
    * )                     usage
                            exit 1
    esac
    shift
done

is_git_repo

check_if_untracked_unstaged_files ## COMMENT TO BYPASS. DEBUG

get_last_commit_hash

get_current_branch_name

do_the_job

########## End of main
