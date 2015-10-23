VDC_NUMBER=${VDC_NUMBER}
API_HEAD=${API_HEAD}
ALLOWED_IP=${ALLOWED_IP}
WDC=${WDC:-wdc1}
BILLING=${BILLING:-true}
function get_cdsserver() {
  grep $WDC"_"$VDC_NUMBER"_cds" vdc.sh | awk '{print $2}'
}
function get_agent() {
  grep $WDC"_"$VDC_NUMBER"_agent" vdc.sh | awk '{print $2}'
}
CDS_SERVER=${CDS_SERVER:-$(get_cdsserver)}
TEST_AGENT=${TEST_AGENT:-$(get_agent)}


