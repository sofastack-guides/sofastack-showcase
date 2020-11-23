#!/bin/bash

# Parse the vars from the env, we are only interested in the envs which key
# starts with ANTCLOUD_
function parse_vars_from_env()
{
    local prefix="ANTCLOUD_"
    local size=${#prefix}
    while read m_env
    do
        if [[ ${m_env:0:size} == $prefix ]]
        then
            local key=`echo ${m_env#$prefix} | cut -d= -f1`
            local value=`echo ${m_env} | cut -d= -f2-`
            construct_sofaboot_args "$key" "$value"
        fi
    done <<< "$(env)"
}

function construct_sofaboot_args()
{
    local key="$1"
    local value="$2"

    case "$key" in
      APP_SVC_NAME)
      echo -e "spring.application.name=$value\n" >> ${SOFA_APP_PROPS_DIR}/${SOFA_APP_PROPS_FILE_NAME}
      ;;
      LOG_LEVEL)
      echo -e "logging.level.com.alipay.sofa=$value\n" >> ${SOFA_APP_PROPS_DIR}/${SOFA_APP_PROPS_FILE_NAME}
      ;;
      LOG_PATH)
      echo -e "logging.path=$value\n" >> ${SOFA_APP_PROPS_DIR}/${SOFA_APP_PROPS_FILE_NAME}
      ;;
      SOFA_INSTANCE_ID)
      SOFA_JAVA_OPTS="${SOFA_JAVA_OPTS} -Dcom.alipay.instanceid=$value"
      ;;
      SOFA_INSTANCE_ENDPOINT)
      SOFA_JAVA_OPTS="${SOFA_JAVA_OPTS} -Dcom.antcloud.antvip.endpoint=$value"
      ;;
      SOFA_INSTANCE_AK)
      SOFA_JAVA_OPTS="${SOFA_JAVA_OPTS} -Dcom.antcloud.mw.access=$value"
      ;;
      SOFA_INSTANCE_SECRET)
      SOFA_JAVA_OPTS="${SOFA_JAVA_OPTS} -Dcom.antcloud.mw.secret=$value"
      ;;
      SOFA_PROFILE)
      SOFA_JAVA_OPTS="${SOFA_JAVA_OPTS} -Dspring.profiles.active=$value"
      APP_PROFILE="$value"
      ;;
      SOFA_RPC_IF)
      SOFA_JAVA_OPTS="${SOFA_JAVA_OPTS} -Drpc_bind_network_interface=$value"
      ;;
      SOFA_RPC_IP_RANGE)
      SOFA_JAVA_OPTS="${SOFA_JAVA_OPTS} -Drpc_enabled_ip_range=$value"
      ;;
      *)
      ;;
    esac
}

function process_profile ()
{
    if [ -z "${APP_PROFILE}" ] && [ -n "${ANTCLOUD_WORKSPACE_NAME}" ]; then
      SOFA_JAVA_OPTS="${SOFA_JAVA_OPTS} -Dspring.profiles.active=${ANTCLOUD_WORKSPACE_NAME}"
    fi
}

SOFA_JAVA_OPTS="${JAVA_OPTS}"
APP_PROFILE=""

SOFA_APP_PROPS_DIR=/home/admin/release/config
SOFA_APP_PROPS_FILE_NAME="application.properties"
mkdir -p ${SOFA_APP_PROPS_DIR}

# TAR jar and copy application.properites
jar_file=`find /home/admin/release/run/ |grep "jar"`
jar -xf $jar_file
cp -a BOOT-INF/classes/config/*.properties ${SOFA_APP_PROPS_DIR}/

# procee the properties
if [ -n "${ANTCLOUD_SOFA_PROFILE}" ]; then
  SOFA_APP_PROPS_FILE_NAME="application-${ANTCLOUD_SOFA_PROFILE}.properties"
else
  if [ -n "${ANTCLOUD_WORKSPACE_NAME}" ]; then
    SOFA_APP_PROPS_FILE_NAME="application-${ANTCLOUD_WORKSPACE_NAME}.properties"
  fi
fi
export SOFA_APP_PROPS_FILE_PATH="${SOFA_APP_PROPS_DIR}/${SOFA_APP_PROPS_FILE_NAME}"
echo "Generate the application profile properties file at: ${SOFA_APP_PROPS_FILE_PATH}"
if [ -f "${SOFA_APP_PROPS_FILE_PATH}" ]; then
  echo "Find the last props file, use it."
  # cat /dev/null > ${SOFA_APP_PROPS_FILE_PATH}
else
  touch ${SOFA_APP_PROPS_FILE_PATH}
fi
chmod 777 ${SOFA_APP_PROPS_FILE_PATH} && chown -R admin:admin ${SOFA_APP_PROPS_DIR}

# Prepare the application-${profile}.properties and JAVA_OPTS
parse_vars_from_env
process_profile

# Start the sofa boot application.
RUN_DIR=/home/admin/release/run
chown -R admin:admin ${RUN_DIR}

export APP_JAVA_OPTS="${SOFA_JAVA_OPTS}"
if [ "${ANTCLOUD_DEPLOY_LEGACY_MODE}" = true ]; then
  echo "Start the application in legacy mode which will start a ssh port 2022."
  /usr/bin/supervisord -c /etc/supervisor/supervisord.conf
else
  echo "Start the application in Cloud native mode."
  # Exclude the sshd and sofaboot-app from supervisord, but still need to start the crontab to run tsar.
  mv /etc/supervisor/conf.d/sshd.conf /etc/supervisor/conf.d/sshd.bac
  mv /etc/supervisor/conf.d/sofaboot-app.conf /etc/supervisor/conf.d/sofaboot-app.bac
  # Run the supervisord at background.
  nohup /usr/bin/supervisord -c /etc/supervisor/supervisord.conf > /tmp/supervisord.log 2>&1 &
  # Switch to user admin to execute the cmd.
  su -c "/home/admin/scripts/sofaboot-start" admin
fi