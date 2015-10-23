BILLING=${BILLING:-true}
echo "billing is "$BILLING
if [ "$BILLING" = "true" ]; then
    java -Dspring.profiles.active=mysql -jar end-to-end-testing.jar $@
else
    java -jar end-to-end-testing.jar $@
fi
