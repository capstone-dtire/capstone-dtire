const Compute = require('@google-cloud/compute')
const compute = new Compute()

exports.startInstancePubSub = async(event, context, callback) = > {
    try {
        const payload = _validatePayload(
            JSON.parse(Buffer.from(event.data, 'base64').toString())
        )
        const options = { filter: `labels.${payload.label}` }
        const[vms] = await compute.getVMs(options)
        await Promise.all(
            vms.map(async (instance) => {
                const [operation] = await compute
                    .zone(instance.zone.id)
                    .vm(instance.name)
                    .start()
                // Operation pending
                return operation.promise()
            })
        )

        // Operation complete. Instance successfully started.
        const message = `Successfully started instance(s)`
        console.log(message)
        callback(null, message)
    } catch(err) {
        console.log(err)
        callback(err)
    }
}

const _validatePayload = (payload) = > {
    if(!payload.label) {
        throw new Error(`Attribute 'label' missing from payload`)
    }
return payload
}
